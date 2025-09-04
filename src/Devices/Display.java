package Devices;

import Devices.DisplayObjects.ButtonCmd;
import Devices.DisplayObjects.TextCmd;
import Message.Message;
import Sockets.commPort;
import Message.MessageReader;

import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.*;

/**
 * Screen UI (sockets only).
 * Inbound (backend → UI):
 *   b:<idx>:(x|m)
 *   t:(00|01|23|45|67|89):s#:f#:c#:<text or left|right>
 * Outbound (UI → backend):
 *   click:<idx>
 */
public class Display {
    private static final Set<String> PAIRS  = Set.of("00","01","23","45","67","89");

    private static class ParsedLine {
        final List<ButtonCmd> buttons = new ArrayList<>();
        final List<TextCmd> texts = new ArrayList<>();
    }

    // ---- UI state
    private final GridPane grid = new GridPane();
    private final Map<Integer, Region> btns = new HashMap<>(); // from 0 to 9
    private final Map<String, Pane> centers = new HashMap<>(); // "00","01","23","45","67","89"
    private final Set<Integer> multiActive = new HashSet<>();  // are 'm'
    private final Label footer = new Label();                  // a status line (will remove later)

    private static final double WIDTH = 960, HEIGHT = 540;

    // IO
    private volatile boolean running = true;
    private volatile commPort port;

    /**
     * Returns entire display
     */
    public VBox createPumpDisplay() {
        buildGrid();
        resetAll();

        VBox root = new VBox();
        root.getChildren().addAll(grid, footerBar());
        VBox.setVgrow(grid, Priority.ALWAYS);

        startIO();

        return root;
    }

    /**
     * Creates initial grid of buttons and displays
     */
    private void buildGrid() {
        grid.setGridLinesVisible(false);
        grid.setStyle("-fx-background-color: white;");
        grid.setPrefSize(WIDTH, HEIGHT - 32);

        ColumnConstraints c0 = new ColumnConstraints();
        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c0.setPercentWidth(16);
        c1.setPercentWidth(68);
        c2.setPercentWidth(16);
        grid.getColumnConstraints().addAll(c0, c1, c2);

        for (int r = 0; r < 5; r++) {
            RowConstraints rr = new RowConstraints();
            rr.setPercentHeight(20);
            grid.getRowConstraints().add(rr);
        }

        String[] pairs = {"01","23","45","67","89"};
        for (int row = 0; row < 5; row++) {
            int leftIdx = row * 2;      // 0,2,4,6,8
            int rightIdx = leftIdx + 1; // 1,3,5,7,9
            String pair = pairs[row];

            Region leftBtn = makeButtonCell(leftIdx);
            Region rightBtn = makeButtonCell(rightIdx);
            Pane center = makeCenterCell(pair);

            btns.put(leftIdx, leftBtn);
            btns.put(rightIdx, rightBtn);
            centers.put(pair, center);

            grid.add(leftBtn, 0, row);
            grid.add(center, 1, row);
            grid.add(rightBtn, 2, row);

            GridPane.setHalignment(leftBtn, HPos.CENTER);
            GridPane.setValignment(leftBtn, VPos.CENTER);
            GridPane.setHalignment(center, HPos.CENTER);
            GridPane.setValignment(center, VPos.CENTER);
            GridPane.setHalignment(rightBtn, HPos.CENTER);
            GridPane.setValignment(rightBtn, VPos.CENTER);
        }
        // Alias "00" to top row center ("01") for early test payloads
        centers.put("00", centers.get("01"));
    }

    private Region makeButtonCell(int idx) {
        StackPane p = new StackPane();
        p.setPrefSize(96, 72);
        p.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        p.setCursor(Cursor.HAND);
        p.setStyle(inactiveButtonStyle());
        p.setDisable(true); // inactive by default
        p.setOnMouseClicked((MouseEvent e) -> {
            if (p.isDisable()) return;
            try {
                if (port != null) port.send(new Message("click:" + idx));
                footer.setText("click:" + idx);
            } catch (IOException ex) {
                log("send failed: " + ex.getMessage());
            }
            if (multiActive.contains(idx)) {
                clearMultiSelections();
                setButtonVisual(p, true, true);
            }
        });
        return p;
    }

    private Pane makeCenterCell(String pair) {
        StackPane center = new StackPane();
        center.setPrefSize(680, 88);
        center.setStyle("-fx-background-color: transparent;");
        return center;
    }

    // Rendering
    private void resetAll() {
        centers.values().forEach(p -> p.getChildren().clear());
        btns.values().forEach(b -> {
            b.setDisable(true);
            setButtonVisual(b, false, false);
        });
        multiActive.clear();
        log("waiting for backend…");
    }

    private void applyButton(ButtonCmd cmd) {
        Region b = btns.get(cmd.idx);
        if (b == null) return;
        b.setDisable(false);
        setButtonVisual(b, true, false);
        if (cmd.multi) multiActive.add(cmd.idx);
    }

    private void applyText(TextCmd cmd) {
        Pane center = centers.get(cmd.pair);
        if (center == null) return;
        center.getChildren().clear();

        if (cmd.split) {
            HBox h = new HBox(16);
            h.setAlignment(Pos.CENTER);

            Label left = labelFrom(cmd.left, cmd);
            Label right = labelFrom(cmd.right, cmd);

            HBox.setHgrow(left, Priority.ALWAYS);
            HBox.setHgrow(right, Priority.ALWAYS);
            left.setMaxWidth(Double.MAX_VALUE);
            right.setMaxWidth(Double.MAX_VALUE);
            left.setAlignment(Pos.CENTER_LEFT);
            right.setAlignment(Pos.CENTER_RIGHT);

            h.getChildren().addAll(left, right);
            center.getChildren().add(h);
        } else {
            Label mid = labelFrom(cmd.text, cmd);
            mid.setAlignment(Pos.CENTER);
            center.getChildren().add(mid);

            // this disables the left button for that row, so that only right button can choose stuff
            int leftIdx = leftIdxForPair(cmd.pair);
            Region leftBtn = btns.get(leftIdx);
            if (leftBtn != null) {
                leftBtn.setDisable(true);
                setButtonVisual(leftBtn, false, false);
                multiActive.remove(leftIdx);
            }
        }
    }

    private Label labelFrom(String content, TextCmd cmd) {
        Label lab = new Label(content == null ? "" : content);
        lab.setWrapText(true);

        if (cmd.styledFromMR != null) {
            Font f = cmd.styledFromMR.getFont();
            javafx.scene.paint.Paint paint = cmd.styledFromMR.getFill();
            if (f != null) lab.setFont(f);
            if (paint != null) lab.setTextFill(paint);
        }
        return lab;
    }

    private void setButtonVisual(Region b, boolean active, boolean selected) {
        if (!active) {
            b.setStyle(inactiveButtonStyle());
            return;
        }
        if (selected) {
            b.setStyle("-fx-background-color: #DBEAFE; -fx-border-color: #3B82F6; -fx-border-width: 2; -fx-border-radius: 12; -fx-background-radius: 12;");
        } else {
            b.setStyle("-fx-background-color: #E5E7EB; -fx-border-color: #94A3B8; -fx-border-width: 1; -fx-border-radius: 12; -fx-background-radius: 12;");
        }
    }
    private String inactiveButtonStyle() {
        return "-fx-background-color: #F3F4F6; -fx-border-color: #CBD5E1; -fx-border-radius: 12; -fx-background-radius: 12;";
    }
    private void clearMultiSelections() {
        for (int idx : multiActive) {
            Region b = btns.get(idx);
            if (b != null) setButtonVisual(b, true, false);
        }
    }

    // ---- left and right index helpers
    private int leftIdxForPair(String pair) {
        switch (pair) {
            case "00":
            case "01": return 0;
            case "23": return 2;
            case "45": return 4;
            case "67": return 6;
            case "89": return 8;
            default: return -1;
        }
    }
    private int rightIdxForPair(String pair) {
        switch (pair) {
            case "00":
            case "01": return 1;
            case "23": return 3;
            case "45": return 5;
            case "67": return 7;
            case "89": return 9;
            default: return -1;
        }
    }

    // Inbound handling
    private void handleInbound(String line) {
        ParsedLine parsed = parseLineUsingMR(line);
        Platform.runLater(() -> {
            for (ButtonCmd b : parsed.buttons) applyButton(b);
            for (TextCmd t : parsed.texts) if (PAIRS.contains(t.pair)) applyText(t);
        });
    }

    // Use MessageReader for text style + regex for buttons/types.
    private ParsedLine parseLineUsingMR(String line) {
        ParsedLine out = new ParsedLine();
        if (line == null || line.isEmpty()) return out;

        MessageReader mr = new MessageReader(line);
        out.buttons.addAll(mr.getButtons());
        out.texts.addAll(mr.getTexts());

        return out;
    }

    private int parseIntSafely(String s, int def) {
        try { return Integer.parseInt(s); } catch (Exception e) { return def; }
    }

    // socket stuff
    private void startIO() {
        Thread io = new Thread(() -> {
            try {
                port = new commPort("screen");
                log("connected");

                while (running) {
                    Message m = port.get();
                    if (m == null) continue;
                    String line = m.toString().trim();
                    if (!line.isEmpty()) handleInbound(line);
                }

            } catch (IOException connectErr) {
                log("connect failed: " + connectErr.getMessage());
            } catch (Exception ex) {
                ex.printStackTrace();
                log("io error: " + ex.getMessage());
            }

        }, "screen-io");
        io.setDaemon(true);
        io.start();
    }

    private void log(String s) {
        Platform.runLater(() -> footer.setText(s));
    }

    private HBox footerBar() {
        footer.setTextFill(Color.web("#374151")); // slate-700
        footer.setStyle("-fx-padding: 8; -fx-font-size: 12px;");
        HBox box = new HBox(footer);
        box.setStyle("-fx-background-color: #F3F4F6; -fx-border-color: #E5E7EB; -fx-border-width: 1 0 0 0;");
        return box;
    }
}

package Devices;

import Sockets.Message;
import Sockets.commPort;
import UIHelper.MessageReader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Changed Display.java so that it uses UIHelper.MessageReader to parse text and
 * lookup their fonts, colors, etc.
 * Inbound (backend → UI):
 *   b:<idx>:(x|m)           // buttons to enable + type (exclusive x / multi m)
 *   t:(00|01|23|45|67|89):s#:f#:c#:<text or left|right>
 * Outbound (UI → backend):
 *   click:<idx> (this is returning the index of the button pressed in a printing
 *                statement. In order for it to "do" anything other than display
 *                the grid, we need to change this)
 */
public class Display extends Application {

    // ---- Layout data
    private static final Set<String> PAIRS = Set.of("00","01","23","45","67","89");
    private static final double WIDTH = 960, HEIGHT = 540;

    // ---- UI state
    private final GridPane grid = new GridPane();
    // Container panes for left/right buttons per index 0..9
    private final Map<Integer, StackPane> btnCells = new HashMap<>();
    // Live buttons currently mounted at each index (so we can restyle/highlight)
    private final Map<Integer, Button> liveButtons = new HashMap<>();
    // Center cells keyed by pair "01","23"... ("00" aliases to "01")
    private final Map<String, Pane> centers = new HashMap<>();
    // Which button indices are multi-choice ('m')
    private final Set<Integer> multiActive = new HashSet<>();
    private final Label footer = new Label();

    // IO
    private volatile boolean running = true;
    private volatile commPort port;

    @Override
    public void start(Stage stage) {
        buildGrid();

        VBox root = new VBox();
        root.getChildren().addAll(grid, footerBar());
        VBox.setVgrow(grid, Priority.ALWAYS);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Screen");
        stage.show();

        startIO();
        log("waiting for backend…");
    }

    @Override
    public void stop() { running = false; }

    // Grid
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

            StackPane leftCell = makeButtonCell(leftIdx);
            StackPane rightCell = makeButtonCell(rightIdx);
            Pane center = makeCenterCell(pair);

            btnCells.put(leftIdx, leftCell);
            btnCells.put(rightIdx, rightCell);
            centers.put(pair, center);

            grid.add(leftCell, 0, row);
            grid.add(center, 1, row);
            grid.add(rightCell, 2, row);

            GridPane.setHalignment(leftCell, HPos.CENTER);
            GridPane.setValignment(leftCell, VPos.CENTER);
            GridPane.setHalignment(center, HPos.CENTER);
            GridPane.setValignment(center, VPos.CENTER);
            GridPane.setHalignment(rightCell, HPos.CENTER);
            GridPane.setValignment(rightCell, VPos.CENTER);
        }
        // alias "00" to top row center
        centers.put("00", centers.get("01"));
    }

    private StackPane makeButtonCell(int idx) {
        StackPane p = new StackPane();
        p.setPrefSize(96, 72);
        p.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        p.setStyle(inactiveButtonStyle());
        p.setDisable(true); // disabled until a 'b:idx' arrives
        return p;
    }

    private Pane makeCenterCell(String pair) {
        StackPane center = new StackPane();
        center.setPrefSize(680, 88);
        center.setStyle("-fx-background-color: transparent;");
        return center;
    }

    // Handle the inbound messages
    private void handleInbound(String line) {
        Platform.runLater(() -> {
            // Use MessageReader for styled nodes
            MessageReader mr = new MessageReader(line);
            Text mrText = mr.getText();                    // styled font/color for this line's t:...
            Map<Integer, Button> mrButtons = mapById(mr);  // map "id string" -> Button

            // Parse minimal metadata we still need (button types + text pair/split)
            Meta meta = parseMeta(line);

            // 1) put the MR Buttons by index and style them to match the current theme
            for (BtnSpec b : meta.buttons) {
                Button btn = mrButtons.get(b.idx);
                placeAndStyleButton(b.idx, btn, b.multi);
            }

            // 2) copy font/color/size from MR's Text into Labels laid out by us
            if (meta.text != null && PAIRS.contains(meta.text.pair)) {
                applyText(meta.text, mrText);
            }
        });
    }

    // TODO: Fix  text box not appearing

    // Map the MessageReader buttons by their numeric id (the button's text is the id string)
    private Map<Integer, javafx.scene.control.Button> mapById(UIHelper.MessageReader mr) {
        Map<Integer, javafx.scene.control.Button> out = new HashMap<>();
        for (javafx.scene.control.Button b : mr.getButtons()) {
            try {
                int id = Integer.parseInt(b.getText());
                out.put(id, b);
            } catch (NumberFormatException ignored) { /* skip non-numeric labels */ }
        }
        return out;
    }

    // button placement and styling
    private void placeAndStyleButton(int idx, javafx.scene.control.Button btn, boolean multi) {
        StackPane cell = btnCells.get(idx);
        if (cell == null) return;

        // use a separate final variable for the lambda
        final Button target = (btn != null) ? btn : new Button("");
        target.setText(""); // <— make the button visually empty

        // handle multi-choice highlight and send the "button clicked" statement
        // TODO: handle the exclusive choice colors and highlight
        final int idForSend = idx;
        target.setOnAction(e -> {
            try {
                if (port != null) port.send(new Message("click:" + idForSend));
                footer.setText("click:" + idForSend);
            } catch (IOException ex) {
                log("send failed: " + ex.getMessage());
            }
            if (multiActive.contains(idForSend)) {
                clearMultiSelections();
                setButtonVisual(target, true, true);
            }
        });

        target.setCursor(Cursor.HAND);
        target.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        // Mount/replace in cell
        cell.getChildren().setAll(target);
        cell.setDisable(false);
        liveButtons.put(idx, target);

        // Track multi-choice and apply base visuals
        if (multi) multiActive.add(idx); else multiActive.remove(idx);
        setButtonVisual(target, true, false);
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
            Button b = liveButtons.get(idx);
            if (b != null) setButtonVisual(b, true, false);
        }
    }

    // renders the text and copies the style from MR text parsed
    private void applyText(TextSpec t, Text mrText) {
        Pane center = centers.get(t.pair);
        if (center == null) return;
        center.getChildren().clear();

        Font f = (mrText != null) ? mrText.getFont() : null;
        Paint paint = (mrText != null) ? mrText.getFill() : null;

        if (t.split) {
            HBox h = new HBox(16);
            h.setAlignment(Pos.CENTER);
            StackPane.setAlignment(h, Pos.CENTER);

            Label left  = new Label(t.left == null ? "" : t.left);
            Label right = new Label(t.right == null ? "" : t.right);
            if (f != null)    { left.setFont(f);  right.setFont(f); }
            if (paint != null){ left.setTextFill(paint); right.setTextFill(paint); }

            HBox.setHgrow(left, Priority.ALWAYS);
            HBox.setHgrow(right, Priority.ALWAYS);
            left.setMaxWidth(Double.MAX_VALUE);
            right.setMaxWidth(Double.MAX_VALUE);
            left.setAlignment(Pos.CENTER_LEFT);
            right.setAlignment(Pos.CENTER_RIGHT);

            h.getChildren().addAll(left, right);
            center.getChildren().add(h);
        } else {
            Label mid = new Label(t.text == null ? "" : t.text);
            if (f != null)    mid.setFont(f);
            if (paint != null)mid.setTextFill(paint);
            StackPane.setAlignment(mid, Pos.CENTER);
            center.getChildren().add(mid);

            // rule: single-column row → disable LEFT button for that row
            int leftIdx = leftIdxForPair(t.pair);
            StackPane leftCell = btnCells.get(leftIdx);
            if (leftCell != null) {
                leftCell.setDisable(true);
                Region leftBtn = liveButtons.get(leftIdx);
                if (leftBtn != null) setButtonVisual(leftBtn, false, false);
                multiActive.remove(leftIdx);
            }
        }
    }

    private int leftIdxForPair(String pair) {
        switch (pair) {
            case "00":
            case "01": return 0;
            case "23": return 2;
            case "45": return 4;
            case "67": return 6;
            case "89": return 8;
            default:   return -1;
        }
    }


    /**
     * MessageReader gives styled nodes, but it doesn’t tell Display:
     *  which button index goes in which grid cell (0/1 → row 1, 2/3 → row 2, etc.),
     *  whether a button is multi-choice (so we can do highlight behavior),
     *  whether a row’s text is split (left|right) or single, which controls:
     *      rendering two labels vs one,
     *      your rule “single-column rows disable the left button”.
     */
    // ---------- Minimal token parsing for metadata we need ----------
    private static final class BtnSpec { final int idx; final boolean multi; BtnSpec(int i, boolean m){idx=i;multi=m;} }
    private static final class TextSpec {
        final String pair; final boolean split; final String left; final String right; final String text;
        TextSpec(String p, boolean s, String l, String r, String t){pair=p;split=s;left=l;right=r;text=t;}
    }
    private static final class Meta { final List<BtnSpec> buttons = new ArrayList<>(); TextSpec text; }

    private Meta parseMeta(String line) {
        Meta meta = new Meta();
        if (line == null || line.isEmpty()) return meta;

        String[] parts = line.split("\\s*,\\s*");
        for (String token : parts) {
            if (token.startsWith("b:")) {
                // b:<idx>[:x|m]
                String[] p = token.split(":", 3);
                int idx = Integer.parseInt(p[1]);
                boolean multi = (p.length >= 3 && "m".equalsIgnoreCase(p[2]));
                meta.buttons.add(new BtnSpec(idx, multi));
            } else if (token.startsWith("t:")) {
                // t:<pair>:s#:f#:c#:<payload>
                String[] p = token.split(":", 6);
                if (p.length < 6) continue;
                String pair = p[1];
                String payload = p[5];
                if (payload.contains("|")) {
                    int i = payload.indexOf('|');
                    meta.text = new TextSpec(pair, true, payload.substring(0,i), payload.substring(i+1), null);
                } else {
                    meta.text = new TextSpec(pair, false, null, null, payload);
                }
            }
        }
        return meta;
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
                log("io error: " + ex.getMessage());
            }
        }, "screen-io");
        io.setDaemon(true);
        io.start();
    }

    private void log(String s) { footer.setText(s); }
    private HBox footerBar() {
        footer.setTextFill(Color.web("#374151"));
        footer.setStyle("-fx-padding: 8; -fx-font-size: 12px;");
        HBox box = new HBox(footer);
        box.setStyle("-fx-background-color: #F3F4F6; -fx-border-color: #E5E7EB; -fx-border-width: 1 0 0 0;");
        return box;
    }

    public static void main(String[] args) { launch(args); }
}

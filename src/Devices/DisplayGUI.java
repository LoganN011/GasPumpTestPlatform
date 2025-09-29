package Devices;

import Devices.DisplayObjects.ButtonCmd;
import Devices.DisplayObjects.TextCmd;
import Message.Message;
import Message.MessageReader;
import Sockets.commPort;
import Utility.MyTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

/**
 * Screen UI (sockets only).
 * Inbound (backend → UI):
 * b:<idx>:(x|m)
 * t:(00|01|23|45|67|89):s#:f#:c#:<text or left|right>
 * Outbound (UI → backend):
 * click:<idx>
 */
public class DisplayGUI extends Application {
    private static final Set<String> PAIRS = Set.of("00", "01", "23", "45", "67", "89");
    // IO
    private volatile boolean running = true;
    private volatile commPort port;

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
    private final StackPane overlayLayer = new StackPane();
    private Label selectedGasLabel;
    private MyTimer timer;


    private static final double WIDTH = 960, HEIGHT = 540;

    /**
     * Starts initial scene by creating DisplayHandler and passing this current
     * instance of Display.
     *
     * @param stage the primary stage for this application, onto which
     *              the application scene can be set.
     */
    public void start(Stage stage) {
        startIO();

        Scene scene = new Scene(this.createPumpDisplay(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Screen");

        stage.setOnCloseRequest(e -> stopIO());
        stage.show();
    }

    /**
     * Returns entire display
     */
    public VBox createPumpDisplay() {
        buildGrid();
        resetAll();

        // Create overlay to overlay dialog messages on top of grid
        overlayLayer.getChildren().setAll(grid);
        overlayLayer.setPickOnBounds(false);
        overlayLayer.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        VBox root = new VBox();
        root.getChildren().addAll(overlayLayer, footerBar());
        VBox.setVgrow(overlayLayer, Priority.ALWAYS);

        return root;
    }

    /**
     * Creates initial grid of buttons and displays
     */
    private void buildGrid() {
        grid.setGridLinesVisible(false);
        grid.setStyle("-fx-background-color: #FFFDF8;"); //f9f9f9
        grid.setPrefSize(WIDTH, HEIGHT - 32);

        // Must clear otherwise grid builds incorrectly
        grid.getChildren().clear();
        grid.getColumnConstraints().clear();
        grid.getRowConstraints().clear();

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

        String[] pairs = {"01", "23", "45", "67", "89"};
        for (int row = 0; row < 5; row++) {
            int leftIdx = row * 2;      // 0,2,4,6,8
            int rightIdx = leftIdx + 1; // 1,3,5,7,9
            String pair = pairs[row];

            Region leftBtn = makeButtonCell(leftIdx);
            Region rightBtn = makeButtonCell(rightIdx);
            Pane center = makeCenterCell();

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
                port.send(new Message(String.valueOf(idx)));

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (multiActive.contains(idx)) {
                clearMultiSelections();
                setButtonVisual(p, true, true);
            }
        });
        return p;
    }

    private Pane makeCenterCell() {
        StackPane center = new StackPane();
        center.setPrefSize(680, 88);
        center.setStyle("-fx-background-color: transparent;");
        return center;
    }

    // Rendering
    public void resetAll() {
        centers.values().forEach(p -> p.getChildren().clear());
        btns.values().forEach(b -> {
            b.setDisable(true);
            setButtonVisual(b, false, false);
        });
        multiActive.clear();
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
            h.setPadding(new Insets(20, 10, 20, 10));
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
            case "01":
                return 0;
            case "23":
                return 2;
            case "45":
                return 4;
            case "67":
                return 6;
            case "89":
                return 8;
            default:
                return -1;
        }
    }

    // Inbound handling
    public void handleInbound(String line) {
        ParsedLine parsed = parseLineUsingMR(line);
        Platform.runLater(() -> {
            //resetAll();

            for (ButtonCmd b : parsed.buttons) applyButton(b);
            for (TextCmd t : parsed.texts)
                if (PAIRS.contains(t.pair)) applyText(t);
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


    /**
     * Creates visual footer on bottom of pump display
     *
     * @return HBox of footer
     */
    private HBox footerBar() {
        footer.setStyle("-fx-padding: 8; -fx-font-size: 12px;");
        HBox box = new HBox(footer);
        box.setStyle("-fx-background-color: #575D90; -fx-border-color: #E5E7EB; -fx-border-width: 1 0 0 0;");
        return box;
    }

    // Return the center text for the row that contains this button index (0..9).
    // Example: idx 2 or 3  -> pair "23" (e.g., "REGULAR 87")
    public String getGasField(int buttonIdx) {
        String pair = pairForButton(buttonIdx);
        if (pair == null) return null;
        return getCenterTextByPair(pair);
    }

    /**
     * Helper: Find middle pair ID
     *
     * @param idx int of ButtonID
     * @return String pair
     */
    private String pairForButton(int idx) {
        switch (idx) {
            case 0:
            case 1:
                return "01";
            case 2:
            case 3:
                return "23";
            case 4:
            case 5:
                return "45";
            case 6:
            case 7:
                return "67";
            case 8:
            case 9:
                return "89";
            default:
                return null;
        }
    }

    /**
     * Helper: Gets center text by pair ID
     *
     * @param pair String ID
     * @return String content
     */
    private String getCenterTextByPair(String pair) {
        Pane center = centers.get(pair);
        if (center == null || center.getChildren().isEmpty()) return null;

        javafx.scene.Node node = center.getChildren().get(0);

        // Single centered label (most gas rows use this)
        if (node instanceof Label) {
            return ((Label) node).getText();
        }

        // Split left|right layout: join both sides with " | "
        if (node instanceof HBox) {
            StringBuilder sb = new StringBuilder();
            for (javafx.scene.Node child : ((HBox) node).getChildren()) {
                if (child instanceof Label) {
                    String t = ((Label) child).getText();
                    if (t != null && !t.isEmpty()) {
                        if (sb.length() > 0) sb.append(" | ");
                        sb.append(t);
                    }
                }
            }
            return sb.toString();
        }

        // Fallback
        return node.toString();
    }

    /**
     * Visually select gas with pill icon, checkmark icon, and pop-in animation.
     */
    public void markSelectedGas(int buttonIdx) {
        Platform.runLater(() -> {
            String pair = pairForButton(buttonIdx);
            if (pair == null) return;

            Label label = findCenterLabel(pair);
            if (label == null) return;

            // Clear any previous selection
            if (selectedGasLabel != null && selectedGasLabel != label) {
                clearSelectedStyle(selectedGasLabel);
            }

            selectedGasLabel = label;
        });
    }

    /**
     * Clears current gas selection (e.g., pressing "Cancel")
     */
    public void clearCurrentGasSelection() {
        Platform.runLater(() -> {
            if (selectedGasLabel != null) {
                clearSelectedStyle(selectedGasLabel);
                selectedGasLabel = null;
            }
        });
    }

    /**
     * Helper: find the single centered Label in the center cell for a pair (e.g., "23").
     */
    private Label findCenterLabel(String pair) {
        Pane center = centers.get(pair);
        if (center == null || center.getChildren().isEmpty()) return null;
        Node node = center.getChildren().get(0);

        if (node instanceof Label) {
            return (Label) node;
        }
        return null;
    }

    /**
     * Remove current gas selection style.
     */
    private void clearSelectedStyle(Label label) {
        String s = label.getStyle();
        if (s != null) {
            label.setStyle("");
        }

        label.setGraphic(null);
        label.setContentDisplay(ContentDisplay.LEFT);
        label.setGraphicTextGap(0);
    }

    public void startIO() {
        Thread io = new Thread(() -> {
            try {
                port = new commPort("screen");
                System.out.println("Display connected");

                while (running) {
                    Message m = port.get();
                    if (m == null) continue;
                    String line = m.toString().trim();
                    if (!line.isEmpty()) {
                        this.handleInbound(line);
                    }
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }, "screen-io");
        io.setDaemon(true);
        io.start();
    }

    public void stopIO() {
        running = false;
    }


}
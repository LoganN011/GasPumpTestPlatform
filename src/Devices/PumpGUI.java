package Devices;

import Message.Message;
import Sockets.monitorPort;
import Sockets.statusPort;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.InputStream;


public class PumpGUI extends Application {

    monitorPort flow;
    statusPort pump;
    boolean pumpOn = false;
    RotateTransition animation;
    Circle gauge;
    int counter = 0;
    Label flowCounter;
    ImageView pumpImage;
    Rectangle line;
    private final double SIZE= 200;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            pump = new statusPort("pump");
            flow = new monitorPort("flow_meter");
        } catch (Exception e) {
            e.printStackTrace();
        }
        BorderPane root = new BorderPane();

        pumpImage = VisualElements.getImage("off.png", (int) (SIZE/2));
        pumpImage.setPreserveRatio(true);
        pumpImage.fitWidthProperty().bind(root.widthProperty().multiply(0.5));
        pumpImage.fitHeightProperty().bind(root.heightProperty().multiply(0.5));

        StackPane flowMeter = new StackPane();
        gauge = new Circle(SIZE/8);
        gauge.setFill(Color.WHITE);
        gauge.setStroke(Color.BLACK);
        gauge.radiusProperty().bind(root.widthProperty().multiply(0.05));

        line = new Rectangle();

        line.widthProperty().bind(root.widthProperty().multiply(0.01));
        line.heightProperty().bind(root.heightProperty().multiply(0.15));

        Rectangle flowBackground = new Rectangle();

        flowBackground.widthProperty().bind(root.widthProperty().multiply(0.25));
        flowBackground.heightProperty().bind(root.heightProperty().multiply(0.25));

        flowMeter.getChildren().addAll(flowBackground,gauge, line);
        VBox flowAssembly = new VBox();

        flowCounter = new Label("0");

        root.heightProperty().addListener((obs, oldVal, newVal) -> {
            flowCounter.setFont(new Font("Lucida Console", newVal.doubleValue() / 20));
        });

        flowCounter.setStyle(
                "-fx-text-fill: red;" +
                        "-fx-background-color: black;" +
                        "-fx-padding: SIZE/40;" +
                        "-fx-border-color: darkred;" +
                        "-fx-border-width: SIZE/200;" +
                        "-fx-border-radius: SIZE/100;"
        );
        Rectangle textBackground = new Rectangle();
        textBackground.widthProperty().bind(root.widthProperty().multiply(0.25));
        textBackground.heightProperty().bind(root.heightProperty().multiply(0.1));

        flowAssembly.getChildren().addAll(new StackPane(textBackground,flowCounter), flowMeter);
        flowAssembly.prefWidthProperty().bind(root.widthProperty());
        flowAssembly.prefHeightProperty().bind(root.heightProperty().multiply(0.6));

        animation = new RotateTransition(Duration.millis(250), line);
        animation.setByAngle(360);
        animation.setCycleCount(RotateTransition.INDEFINITE);


        line.rotateProperty().addListener((obs, oldVal, newVal) -> {
            if (pumpOn && oldVal.doubleValue() > 300 && newVal.doubleValue() < 60) {
                counter++;
                Platform.runLater(() -> flowCounter.setText("" + counter));
            }
        });

        Image backgroundImage = VisualElements.getImage("pump_background.png");

        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(
                100,
                100,
                true,
                true,
                false,
                true
        ));


        root.setPadding(new Insets(10));
        root.setMinSize(SIZE, SIZE);
        root.setBottom(pumpImage);
        BorderPane.setAlignment(pumpImage, Pos.CENTER);
        root.setTop(flowAssembly);
        root.setBackground(new Background(background));
        flowAssembly.prefWidthProperty().bind(root.widthProperty());
        flowAssembly.prefHeightProperty().bind(root.heightProperty().multiply(0.8));

        Scene scene = new Scene(root, 500, 500);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Pump Assembly");
        primaryStage.show();

        new Thread(() -> {
            while (true) {
                Message msg = flow.read();
                if (msg == null) {
                    continue;
                }

//                if (msg.equals("flow") && pumpOn) { // maybe delete this && if we still want to input that there is not flow
                if (pumpOn) {
//                    flow.send(new Message("flow:" + counter));
                    flow.send(new Message(String.valueOf(counter)));

                } else if (msg.toString().equals("reset")) {
                    if (counter != 0) {
                        resetFlow();
                    }
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
                Message msg = pump.read();
                if (msg == null) {
                    continue;
                }
                String[] msgContent = msg.toString().split(":");
                if (msgContent[0].equals("on") && !pumpOn) {
                    System.out.println("pumping: " + msgContent[1]);
                    togglePump();
                    toggleFlow();
                } else if (msgContent[0].equals("off") && pumpOn) {
                    togglePump();
                    toggleFlow();
                }
            }

        }).start();

    }

    private void toggleFlow() {
        if (pumpOn) {
            Platform.runLater(() -> {
                gauge.setFill(Color.YELLOW);
                line.setRotate(0);
                animation.playFromStart();
            });
        } else {
            Platform.runLater(() -> {
                gauge.setFill(Color.WHITE);
                animation.stop();
            });
        }
    }

    private void resetFlow() {
        counter = 0;
        Platform.runLater(() -> {
            flowCounter.setText("" + 0);

        });
    }

    private void togglePump() {
        pumpOn = !pumpOn;
        if (pumpOn) {
            Platform.runLater(() -> {
                pumpImage.setImage(VisualElements.getImage("on.png"));
            });

        } else {
            Platform.runLater(() -> {
                pumpImage.setImage(VisualElements.getImage("off.png"));
            });
        }
    }
}

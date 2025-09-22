package Devices;

import Message.Message;
import Sockets.monitorPort;
import Sockets.statusPort;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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

        pumpImage = VisualElements.getImage("off.png", (int) (SIZE/2));
        pumpImage.setFitHeight(SIZE/2);
        pumpImage.setFitWidth(SIZE/2);

        StackPane flowMeter = new StackPane();
        gauge = new Circle(SIZE/8);
        gauge.setFill(Color.WHITE);
        gauge.setStroke(Color.BLACK);
        line = new Rectangle(SIZE/40, SIZE/4);
        Rectangle background = new Rectangle(SIZE/3.2,SIZE/3.2);

        flowMeter.getChildren().addAll(background,gauge, line);
        VBox flowAssembly = new VBox();

        flowCounter = new Label("0");

        flowCounter.setFont(new Font("Lucida Console",SIZE/16));
        flowCounter.setStyle(
                "-fx-text-fill: red;" +
                        "-fx-background-color: black;" +
                        "-fx-padding: SIZE/40;" +
                        "-fx-border-color: darkred;" +
                        "-fx-border-width: SIZE/200;" +
                        "-fx-border-radius: SIZE/100;"
        );
        Rectangle textBackground = new Rectangle(SIZE/3.2,SIZE/8);
        flowAssembly.getChildren().addAll(new StackPane(textBackground,flowCounter), flowMeter);

        animation = new RotateTransition(Duration.millis(250), line);
        animation.setByAngle(360);
        animation.setCycleCount(RotateTransition.INDEFINITE);


        line.rotateProperty().addListener((obs, oldVal, newVal) -> {
            if (pumpOn && oldVal.doubleValue() > 300 && newVal.doubleValue() < 60) {
                counter++;
                Platform.runLater(() -> flowCounter.setText("" + counter));
            }
        });


        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setMinSize(SIZE, SIZE);
        root.setCenter(pumpImage);
        root.setRight(flowAssembly);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pump Assembly");
        primaryStage.show();

        new Thread(() -> {

            while (true) {
                Message msg = flow.read();
                if (msg == null) {
                    continue;
                }
                if (msg.equals("flow") && pumpOn) { // maybe delete this && if we still want to input that there is not flow

                    flow.send(new Message("flow:" + counter));

                } else if (msg.equals("reset")) {
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

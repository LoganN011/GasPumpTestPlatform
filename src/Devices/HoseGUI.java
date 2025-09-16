package Devices;

import Message.Message;
import Sockets.controlPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HoseGUI extends Application {

    private boolean connected;
    private boolean full;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        controlPort control = null;
        try {
            control = new controlPort("hose");
        } catch (Exception e) {
            e.printStackTrace();
        }

        controlPort harness = control;
        ProgressBar gasTank = new ProgressBar();
        gasTank.setProgress((Math.random()));
        gasTank.setStyle("-fx-accent: yellow;");
        gasTank.setRotate(-90);
        gasTank.setLayoutX(725);
        gasTank.setLayoutY(200);

        Button clear = new Button("Empty Tank");

        clear.setOnAction(e -> {
            gasTank.setProgress(0);
            full = false;
            if (connected) {
                harness.send(new Message("connected"));
            }
            else  {
                harness.send(new Message("disconnected"));
            }
        });

        clear.setLayoutX(725);
        clear.setLayoutY(300);

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            if (gasTank.getProgress() < 1.0) {
                gasTank.setProgress(gasTank.getProgress() + 0.01);
                System.out.println(gasTank.getProgress());
            } else {
                if (!full) {
                    full = true;
                    harness.send(new Message("full_tank"));
                }
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);

        Circle pumpHandle = new Circle(25);
        pumpHandle.setCenterX(100);
        pumpHandle.setCenterY(150);

        Line hoseLine = new Line();
        hoseLine.setStartX(100);
        hoseLine.setStartY(25);
        hoseLine.setEndX(100);
        hoseLine.setEndY(15);
        hoseLine.setStrokeWidth(2);


        pumpHandle.setOnMouseDragged(e -> {
            pumpHandle.setCenterX(e.getX());
            pumpHandle.setCenterY(e.getY());
            hoseLine.setEndX(e.getX());
            hoseLine.setEndY(e.getY());
        });
        pumpHandle.setOnMouseReleased(e -> {
            if (e.getX() >= 700) {
                System.out.println("on car");
                connected = true;
                if (full) {
                    harness.send(new Message("full_tank"));
                } else {
                    harness.send(new Message("connected"));
                }

                pumpHandle.setCenterX(700);
                pumpHandle.setCenterY(200);
                animation.playFromStart();
                hoseLine.setEndX(700);
                hoseLine.setEndY(200);
            } else {
                if (connected) {
                    animation.stop();
                    connected = false;
                    harness.send(new Message("disconnected"));
                    pumpHandle.setCenterX(100);
                    pumpHandle.setCenterY(150);
                    hoseLine.setEndX(100);
                    hoseLine.setEndY(150);
                }

            }
        });

        Rectangle pump = new Rectangle(100, 400);
        pump.setStyle("-fx-fill: #FF0000");


        Rectangle car = new Rectangle(100, 400);
        car.setStyle("-fx-fill: grey");
        car.setX(700);

        Pane root = new Pane();
        root.setPrefSize(800, 400);
        root.getChildren().addAll(pump, car,hoseLine, pumpHandle, gasTank, clear);
//        root.getChildren().addAll(pump, car, pumpHandle, gasTank, clear);


        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hose");
        primaryStage.show();
    }
}

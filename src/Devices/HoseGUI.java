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
    private final double SIZE= 400;

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
        gasTank.setStyle("-fx-accent: yellow;-fx-control-inner-background: black;");
        gasTank.setRotate(-90);
        gasTank.setLayoutX((SIZE - (SIZE/8))+(SIZE/32));
        gasTank.setLayoutY(SIZE/4);
        //resize gas tank
        gasTank.setPrefWidth(SIZE/8);


        Button clear = new Button("Empty");
        clear.setPrefWidth(SIZE / 10);
        clear.setPrefHeight(SIZE / 24);


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

        clear.setLayoutX((SIZE - (SIZE/8))+(SIZE/32));
        clear.setLayoutY(SIZE/3);

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

        Circle pumpHandle = new Circle(SIZE/32);
        pumpHandle.setCenterX(SIZE/8);
        pumpHandle.setCenterY(SIZE/5);

        Line hoseLine = new Line();
        hoseLine.setStartX(SIZE/8);
        hoseLine.setStartY(SIZE/32);
        hoseLine.setEndX(pumpHandle.getCenterX());
        hoseLine.setEndY(pumpHandle.getCenterY());
        hoseLine.setStrokeWidth(2);


        pumpHandle.setOnMouseDragged(e -> {
            pumpHandle.setCenterX(e.getX());
            pumpHandle.setCenterY(e.getY());
            hoseLine.setEndX(e.getX());
            hoseLine.setEndY(e.getY());
        });
        pumpHandle.setOnMouseReleased(e -> {
            if (e.getX() >= (SIZE - (SIZE/8))) {
                System.out.println("on car");
                connected = true;
                if (full) {
                    harness.send(new Message("full_tank"));
                } else {
                    harness.send(new Message("connected"));
                }

                pumpHandle.setCenterX(SIZE - (SIZE/8));
                pumpHandle.setCenterY(SIZE/4);
                animation.playFromStart();
                hoseLine.setEndX(SIZE - (SIZE/8));
                hoseLine.setEndY(SIZE/4);
            } else {
                if (connected) {
                    animation.stop();
                    connected = false;
                    harness.send(new Message("disconnected"));
                    pumpHandle.setCenterX(SIZE/8);
                    pumpHandle.setCenterY(SIZE/5);
                    hoseLine.setEndX(SIZE/8);
                    hoseLine.setEndY(SIZE/5);
                }

            }
        });

        Rectangle pump = new Rectangle(SIZE/8, SIZE/2);
        pump.setStyle("-fx-fill: #FF0000");


        Rectangle car = new Rectangle(SIZE/8, SIZE/2);
        car.setStyle("-fx-fill: grey");
        car.setX(SIZE - (SIZE/8));

        Pane root = new Pane();
        root.setPrefSize(SIZE, SIZE/2);
        root.getChildren().addAll(pump, car,hoseLine, pumpHandle, gasTank, clear);
//        root.getChildren().addAll(pump, car, pumpHandle, gasTank, clear);


        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hose");
        primaryStage.show();
    }
}

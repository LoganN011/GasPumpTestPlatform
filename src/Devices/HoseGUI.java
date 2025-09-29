package Devices;

import Message.Message;
import Sockets.controlPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
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

        double SIZE = 400;

        Pane root = new Pane();
        Scene scene = new Scene(root, SIZE*1.25,SIZE*1.25);


        double rootHeight = root.getHeight();
        double rootWidth = root.getWidth();
        double sceneW = scene.getHeight();
        double sceneH = scene.getWidth();

        controlPort port = control;
        ProgressBar gasTank = new ProgressBar();
        gasTank.setProgress((Math.random()));
        gasTank.setStyle("-fx-accent: yellow;-fx-control-inner-background: black;");
        gasTank.setRotate(-90);
        gasTank.setLayoutX((rootWidth - (rootWidth/8))+(rootWidth/32));
        gasTank.setLayoutY(rootHeight/4);
        //resize gas tank
        gasTank.setPrefWidth(rootWidth/8);


        Button clear = new Button("Empty");
        clear.setPrefWidth(SIZE / 10);
        clear.setPrefHeight(SIZE / 24);


        clear.setOnAction(e -> {
            gasTank.setProgress(0);
            full = false;
            if (connected) {
                port.send(new Message("connected"));
            }
            else  {
                port.send(new Message("disconnected"));
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
                    port.send(new Message("full_tank"));
                }
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);

        Circle pumpHandle = new Circle(SIZE/32);
        pumpHandle.setCenterX(sceneW/7);
        pumpHandle.setCenterY(sceneH/2);

        Line hoseLine = new Line();
        hoseLine.setStartX(sceneW/7);
        hoseLine.setStartY(sceneH/7);
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
            if (e.getX() >= (sceneW - (sceneW/2))) {
                System.out.println("on car");
                connected = true;
                if (full) {
                    port.send(new Message("full_tank"));
                } else {
                    port.send(new Message("connected"));
                }

                pumpHandle.setCenterX(sceneW - (sceneW/4));
                pumpHandle.setCenterY(sceneH/2);
                animation.playFromStart();
                hoseLine.setEndX(sceneW - (sceneW/4));
                hoseLine.setEndY(sceneH/2);
            } else {
                if (connected) {
                    animation.stop();
                    connected = false;
                    port.send(new Message("disconnected"));
                    pumpHandle.setCenterX(sceneW/7);
                    pumpHandle.setCenterY(sceneH/2);
                    hoseLine.setEndX(pumpHandle.getCenterX());
                    hoseLine.setEndY(pumpHandle.getCenterY());
                }

            }
        });

        Image backgroundImage = VisualElements.getImage("hose.png");

        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(
                SIZE/8,
                SIZE/8,
                true,
                true,
                false,
                true
        ));


        root.setPrefSize(SIZE, SIZE/2);
        root.getChildren().addAll(hoseLine, pumpHandle, gasTank, clear);
        root.setBackground(new Background(background));


        // Screen dimensions
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        primaryStage.setX(screenWidth/15);
        primaryStage.setY(screenHeight/3);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hose");
        primaryStage.show();
    }
}

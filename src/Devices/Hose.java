package Devices;

import Message.Message;
import Sockets.controlPort;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Hose extends Application {

    private boolean connected;
    private boolean full;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage)  {
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

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(100), event ->{
            if (gasTank.getProgress() < 1.0) {
                gasTank.setProgress(gasTank.getProgress() + 0.01);
                System.out.println(gasTank.getProgress());
            } else {
                if(!full){
                    full=true;
                    try{
                        harness.send(new Message("full_tank"));

                    }catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }));
        animation.setCycleCount(Timeline.INDEFINITE);

        Circle pumpHandle = new Circle(25);
        pumpHandle.setCenterX(100);
        pumpHandle.setCenterY(150);


        pumpHandle.setOnMouseDragged(e ->{
            pumpHandle.setCenterX(e.getX());
            pumpHandle.setCenterY(e.getY());

        });
        pumpHandle.setOnMouseReleased(e -> {
            try{
                if(e.getX() >= 700) {
                    System.out.println("on car");
                    connected = true;
                    harness.send(new Message("connected"));
                    pumpHandle.setCenterX(700);
                    pumpHandle.setCenterY(200);
                    animation.playFromStart();
                }
                else {
                    if(connected) {
                        animation.stop();
                        connected = false;
                        harness.send(new Message("disconnected"));
                    }

                }
            }catch(Exception ex){
                ex.printStackTrace();
            }

        });

        Rectangle pump = new Rectangle(100,400);
        pump.setStyle("-fx-fill: #FF0000");


        Rectangle car = new Rectangle(100,400);
        car.setStyle("-fx-fill: grey");
        car.setX(700);

        Pane root = new Pane();
        root.setPrefSize(800,400);
        root.getChildren().addAll(pump,car,pumpHandle,gasTank);





        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hose");
        primaryStage.show();
    }
}

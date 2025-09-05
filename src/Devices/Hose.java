package Devices;

import Message.Message;
import Sockets.controlPort;
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

import java.io.IOException;

public class Hose extends Application {

    private int gasTank;
    private boolean connected;

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
        Button button = new Button("Connect Hose");
        button.setOnAction(e -> {
            try {
                if(button.getText().equals("Connect Hose")) {
                    harness.send(new Message("CONNECTED"));
                    button.setText("Disconnect Hose");
                }
                else {
                    harness.send(new Message("DISCONNECTED"));
                    button.setText("Connect Hose");
                }
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
//        ProgressBar gasTank = new ProgressBar();
//        gasTank.setProgress((Math.random()));
//        gasTank.setStyle("-fx-accent: yellow;");
//        gasTank.setRotate(90);

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
                }
                else {
                    if(connected) {
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
        root.getChildren().addAll(pump,car,pumpHandle);





        Scene scene = new Scene(root);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Hose");
        primaryStage.show();
    }
}

package Devices;

import Message.Message;
import Sockets.monitorPort;
import Sockets.statusPort;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;


public class Pump extends Application {

    monitorPort flow;
    statusPort pump;
    boolean on=false;
    RotateTransition animation;
    Circle gauge;
    int counter=0;
    double lastAngle=0;
    Label flowCounter;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try{
            pump= new statusPort("pump");
            flow= new monitorPort("flow_meter");
        }catch(Exception e){
            e.printStackTrace();
        }

        Rectangle pumpDevice = new Rectangle(200, 150);
        pumpDevice.setFill(Color.GRAY);

        StackPane flowMeter = new StackPane();
        gauge = new Circle(50);
        gauge.setFill(Color.TRANSPARENT);
        gauge.setStroke(Color.BLACK);
        Rectangle line = new Rectangle(10, 100);
        flowMeter.getChildren().addAll(gauge,line);
        VBox flowAssembly = new VBox(10);
         flowCounter = new  Label("0");
        flowAssembly.getChildren().addAll(flowCounter,flowMeter);

        animation = new RotateTransition(Duration.millis(250), line);
        animation.setByAngle(360);
        animation.setCycleCount(RotateTransition.INDEFINITE);



        line.rotateProperty().addListener((obs, oldVal, newVal)->{
            if (lastAngle > 300 && newVal.doubleValue() < 60) {
                counter++;
                flowCounter.setText("" + counter);
            }
            lastAngle = newVal.doubleValue();
        });


        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setMinSize(300, 300);
        root.setCenter(pumpDevice);
        root.setRight(flowAssembly);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pump Assembly");
        primaryStage.show();

        new Thread(() -> {
            try {
                Thread.sleep(100);
            }catch (Exception e){
                e.printStackTrace();
            }
            while(true){

                if(flow.read().equals("on")&&!on){
                    togglePower();
                }
                else if(flow.read().equals("off")&&on){
                    try {
                        flow.send(new Message(""+counter));
                        togglePower();
                        counter=0;

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

    }

    private void togglePower() {
        on = !on;
        if (on) {
            gauge.setFill(Color.YELLOW);
            animation.play();
        } else {
            gauge.setFill(Color.TRANSPARENT);
            animation.stop();
            flowCounter.setText("" + counter);
        }
    }
}

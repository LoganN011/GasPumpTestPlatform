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
import javafx.scene.layout.BorderPane;
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
    boolean flowOn =false;
    boolean pumpOn =false;
    RotateTransition animation;
    Circle gauge;
    int counter=0;
    Label flowCounter;
    Rectangle pumpRec;
    Label pumpLabel;
    Rectangle line;
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

        StackPane pumpDevice = new StackPane();
        pumpRec = new  Rectangle(200,150);
        pumpRec.setFill(Color.GREY);
        pumpLabel = new Label("Pump OFF");
        pumpDevice.getChildren().addAll(pumpRec,pumpLabel);

        StackPane flowMeter = new StackPane();
        gauge = new Circle(50);
        gauge.setFill(Color.TRANSPARENT);
        gauge.setStroke(Color.BLACK);
        line = new Rectangle(10, 100);
        flowMeter.getChildren().addAll(gauge,line);
        VBox flowAssembly = new VBox(10);
        flowCounter = new  Label("0");
        flowAssembly.getChildren().addAll(flowCounter,flowMeter);

        animation = new RotateTransition(Duration.millis(250), line);
        animation.setByAngle(360);
        animation.setCycleCount(RotateTransition.INDEFINITE);



        line.rotateProperty().addListener((obs, oldVal, newVal) -> {
            if (flowOn && oldVal.doubleValue() > 300 && newVal.doubleValue() < 60) {
                counter++;
                Platform.runLater(() -> flowCounter.setText("" + counter));
            }
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

            while(true){
                //Update to take on and something else
                //The something else is either reset the flow counter or send a message that is the amount of flow
                Message msg= flow.read();
                if(msg == null) {
                    continue;
                }
                if(msg.equals("on")&&!flowOn){
                    toggleFlow();
                }
                else if(msg.equals("off")&& flowOn){
                    try {
                        flow.send(new Message(""+counter));
                        toggleFlow();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        new Thread(() -> {
            while(true){
                Message msg= pump.read();
                if(msg == null) {
                    continue;
                }
                if(msg.equals("on")&&!pumpOn){
                    togglePump();
                }
                else if(msg.equals("off")&& pumpOn){
                    togglePump();
                }
            }

        }).start();

    }

    private void toggleFlow() {
        flowOn = !flowOn;

        if (flowOn) {
            counter = 0;
            Platform.runLater(() -> {
                flowCounter.setText("0");
                gauge.setFill(Color.YELLOW);
                line.setRotate(0);
                animation.playFromStart();
            });
        } else {
            Platform.runLater(() -> {
                gauge.setFill(Color.TRANSPARENT);
                animation.stop();
            });
        }
    }

    private void togglePump() {
        pumpOn = !pumpOn;
        if (pumpOn) {
            Platform.runLater(() -> {
                pumpRec.setFill(Color.GREEN);
                pumpLabel.setText("Pump ON");
            });

        } else {
            Platform.runLater(() -> {
                pumpRec.setFill(Color.GREY);
                pumpLabel.setText("Pump OFF");
            });
        }
    }
}

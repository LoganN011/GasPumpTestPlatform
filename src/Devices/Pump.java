package Devices;

import Sockets.monitorPort;
import Sockets.statusPort;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


public class Pump extends Application {

    monitorPort flow;
    statusPort pump;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Rectangle pumpDevice = new Rectangle(200, 150);
        pumpDevice.setFill(Color.GRAY);

        StackPane flowMeter = new StackPane();
        Circle gauge = new Circle(50, Color.WHITE);
        Label gaugeDisplay = new Label("something");
        flowMeter.getChildren().addAll(gauge, gaugeDisplay);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setMinSize(300, 300);
        root.setCenter(pumpDevice);
        root.setRight(flowMeter);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Pump Assembly");
        primaryStage.show();

        Thread io = new Thread(() ->{
//            handleMessage()
        });
        io.start();
    }
}

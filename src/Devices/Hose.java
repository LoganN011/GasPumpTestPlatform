package Devices;

import Message.Message;
import Sockets.controlPort;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Hose extends Application {

    private int gasTank;

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
        BorderPane root = new BorderPane();
        root.setPrefSize(200,200);
        root.setCenter(button);


        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Hose");
        primaryStage.show();
    }
}

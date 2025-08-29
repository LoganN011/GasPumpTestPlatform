package Devices;

import Sockets.Message;
import Sockets.commPort;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;

public class Display extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //todo replace with real handing of connection failing
        commPort self = null;
        try {
            self = new commPort("screen");
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Button button = new Button("Send Message TO Tester");
        button.setMinSize(100, 100);
        Label label = new Label("I should display incoming messages");
        label.setText(self.get().toString());

        commPort finalSelf = self; //TODO: fix this cause idk wants it want this way only sometimes
        button.setOnMouseClicked(x -> {

            try {
                finalSelf.send(new Message("message from display"));
                label.setText(finalSelf.get().toString());
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
                //TODO: put real error handling
            }

        });

        HBox root = new HBox();
        root.setMinSize(300, 300);
        root.getChildren().addAll(button, label);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Screen");
        primaryStage.show();
    }
}

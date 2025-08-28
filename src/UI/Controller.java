package UI;

import Sockets.API_NAME;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;


public class Controller extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        API_NAME self = new API_NAME(1234);

        Button input = new Button("clicking ");
        Label text = new Label("inputs here");

        HBox root = new HBox();
        root.getChildren().addAll(input, text);
        root.setMinSize(300, 300);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("testing");
        primaryStage.show();
    }
}

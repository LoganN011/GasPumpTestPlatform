package UI;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import Sockets.*;

public class Card extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //todo replace this with an instance of API
        API_NAME self = new API_NAME("localHost",1234);


        Button test = new Button();
        test.setMinSize(100, 100);
        test.setOnMouseClicked(x ->{
            String cardNumber = "";
            while (cardNumber.length() < 20) cardNumber += (int)(Math.random() * 10);
            System.out.println(cardNumber);

            //todo relay message through communication API containing number
            //  may look like this:
            self.sendMessage(cardNumber);
        });

        HBox root = new HBox();
        root.getChildren().add(test);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gas Nozzle");
        primaryStage.show();
    }
}

package Devices;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import Sockets.*;

import javax.imageio.IIOException;
import java.io.IOException;

public class Card extends Application {


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //todo replace with real handing of connection failing
        controlPort self = null;
        try{
            self = new controlPort("card");
        } catch (IOException e){
            e.printStackTrace();
            System.exit(1);
        }



        Button test = new Button();
        test.setMinSize(100, 100);
        controlPort finalSelf = self; //TODO: fix this cause idk wants it want this way only sometimes
        test.setOnMouseClicked(x ->{
            String cardNumber = "";
            while (cardNumber.length() < 20) cardNumber += (int)(Math.random() * 10);

            //todo relay message through communication API containing number
            //  may look like this:
            try{
                finalSelf.send(new Message(cardNumber));
            } catch (IOException e){
                e.printStackTrace();
                System.exit(1);
                //TODO: put real error handling
            }

        });

        HBox root = new HBox();
        root.getChildren().add(test);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gas Nozzle");
        primaryStage.show();
    }
}

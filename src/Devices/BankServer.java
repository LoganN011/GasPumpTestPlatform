package Devices;

import Message.Message;
import Sockets.commPort;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import jdk.jshell.execution.JdiExecutionControl;

import java.io.IOException;

public class BankServer extends Application {
    commPort server;
    TextArea log;
    Button approve, decline;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        server = new commPort("bank");

        BorderPane root = new BorderPane();
        root.setPrefSize(400, 400);
        root.setPadding(new Insets(10));
        root.setBackground(VisualElements.ROOT_BACKGROUND);

        log = new TextArea("Incoming requests will appear here:\n");
        log.setEditable(false);
        log.setBorder(VisualElements.THICK_BORDER);
        log.setFocusTraversable(false);
        log.setWrapText(true);
        root.setCenter(log);

        HBox buttons = new HBox();
        buttons.setPrefWidth(root.getPrefWidth());
        approve = new Button("Approve");
        approve.setDisable(true);
        decline = new Button("Decline");
        decline.setDisable(true);
        approve.setOnMouseClicked(x -> card("approved"));
        decline.setOnMouseClicked(x -> card("declined"));
        approve.setPrefWidth(buttons.getPrefWidth()/2);
        decline.setPrefWidth(buttons.getPrefWidth()/2);
        approve.setBorder(VisualElements.THIN_BORDER);
        decline.setBorder(VisualElements.THIN_BORDER);
        approve.setBackground(VisualElements.ELEMENT_BACKGROUND);
        decline.setBackground(VisualElements.ELEMENT_BACKGROUND);
        approve.setOnMouseEntered(x -> approve.setBackground(VisualElements.ACTIVE_ELEMENT));
        decline.setOnMouseEntered(x -> decline.setBackground(VisualElements.ACTIVE_ELEMENT));
        approve.setOnMouseExited(x -> approve.setBackground(VisualElements.ELEMENT_BACKGROUND));
        decline.setOnMouseExited(x -> decline.setBackground(VisualElements.ELEMENT_BACKGROUND));
        buttons.getChildren().addAll(approve, decline);
        root.setBottom(buttons);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Bank Server");
        primaryStage.show();

        Thread io = new Thread(() -> {
           while(true) {
               handleMessage(server.get());
           }
        });
        io.start();

    }

    private void handleMessage(Message message) {
        String[] request = message.toString().split(":");
        String requestType = request[0];
        String requestInfo = request[1];
        switch (requestType.toLowerCase()){
            case "card" -> {
                updateLog("Incoming Request For Card: " + requestInfo + ".");
                approve.setDisable(false);
                decline.setDisable(false);
            }
            case "sale" -> {
                updateLog("Sale completed for $" + Gas.displayPrice(requestInfo));
            }
        }

    }

    private void card(String status) {
        approve.setDisable(true);
        decline.setDisable(true);
        try {
            server.send(new Message(status));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        updateLog("Sending " + status);
    }

    private void updateLog(String message) {
        log.setText(log.getText() + "\n"+ message);
    }

}

package Devices;

import Message.Message;
import Sockets.commPort;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class BankServerGUI extends Application {
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
        approve.setPrefWidth(buttons.getPrefWidth() / 2);
        decline.setPrefWidth(buttons.getPrefWidth() / 2);
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

        // Screen dimensions
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
        primaryStage.setX(screenWidth / 1.5);
        primaryStage.setY(screenHeight / 15);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Bank Server");
        primaryStage.show();

        Thread io = new Thread(() -> {
            while (true) {
                handleMessage(server.get());
            }
        });
        io.start();

    }

    private void handleMessage(Message message) {
        String[] request = message.toString().split(":");
        String requestType = request[0];
        String requestInfo = request[1];
        switch (requestType.toLowerCase()) {
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
        server.send(new Message(status));

        updateLog("Sending " + status);
    }

    private void updateLog(String message) {
        log.setText(log.getText() + "\n" + message);
    }

}

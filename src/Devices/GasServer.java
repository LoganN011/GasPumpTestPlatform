package Devices;

import Message.Message;
import Sockets.commPort;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class GasServer extends Application {
    private ArrayList<Gas> fuels;
    private double totalSales;
    private int salesCount;

    TextArea log;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        commPort server = new commPort("gas_server");

        BorderPane root = new BorderPane();
        root.setPrefSize(400, 400);
        root.setBackground(VisualElements.ROOT_BACKGROUND);
        root.setPadding(new Insets(10));

        log = new TextArea();
        log.setPrefWidth(root.getPrefWidth());
        log.setEditable(false);
        log.setBorder(VisualElements.THICK_BORDER);
        log.setBackground(VisualElements.ELEMENT_BACKGROUND);
        log.setText("Messages regarding sales will appear below:\n");
        root.setTop(log);

        VBox fuelInputs = new VBox();
        fuelInputs.getChildren().addAll(generateFuelInputs(3));
        root.setCenter(fuelInputs);

        HBox buttons = new HBox();
        buttons.setPrefWidth(root.getPrefWidth());
        Button sendInputs = new Button("Set Prices");
        sendInputs.setOnMouseClicked(x -> {
            fuels = readFuelInputs(fuelInputs);
            try{
                server.send(generateCurrentPrices());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sendInputs.setPrefWidth(buttons.getPrefWidth()/2);
        sendInputs.setBackground(VisualElements.ELEMENT_BACKGROUND);
        sendInputs.setOnMouseEntered(x -> sendInputs.setBackground(VisualElements.ACTIVE_ELEMENT));
        sendInputs.setOnMouseExited(x -> sendInputs.setBackground(VisualElements.ELEMENT_BACKGROUND));
        Button addFuel = new Button("Add New Fuel");
        addFuel.setOnMouseClicked(x -> {
            fuelInputs.getChildren().addAll(generateFuelInputs(1));
        });
        addFuel.setPrefWidth(buttons.getPrefWidth()/2);
        addFuel.setBackground(VisualElements.ELEMENT_BACKGROUND);
        addFuel.setOnMouseEntered(x -> addFuel.setBackground(VisualElements.ACTIVE_ELEMENT));
        addFuel.setOnMouseExited(x -> addFuel.setBackground(VisualElements.ELEMENT_BACKGROUND));
        buttons.getChildren().addAll(sendInputs, addFuel);
        root.setBottom(buttons);

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gas Server");
        primaryStage.show();

        Thread io = new Thread(() -> {
            while(true) {
                handleMessage(server.get());
            }
        });
        io.start();

    }

    private void updateLog(String message) {
        log.setText(log.getText() + "\n" + message);
    }

    private Message generateCurrentPrices() {
        String result = "";
        for(Gas cur: fuels) {
            result += cur.toString();
        }
        return new Message(result);
    }

    private ArrayList<Gas> readFuelInputs(VBox fuelInputs) {
        ArrayList<Gas> fuels = new ArrayList<>();
        for (Node child : fuelInputs.getChildren()) {
            HBox fuelSection = (HBox) child;
            TextField fuelNameInput = (TextField) fuelSection.getChildren().getFirst();
            TextField fuelPriceInput = (TextField) fuelSection.getChildren().get(1);
            String name = fuelNameInput.getText();
            String price = fuelPriceInput.getText();
            try {
                if (name.isEmpty() || price.isEmpty()) continue;
                fuels.add(new Gas(name, Double.parseDouble(price), 100));
            } catch (Exception e) {
                System.out.println("Invalid input in text entry, skipping");
                continue;
            }

        }
        fuelInputs.getChildren().clear();
        fuelInputs.getChildren().addAll(generateFuelInputs(Math.max(fuels.size(), 3)));
        return fuels;
    }

    private ArrayList<HBox> generateFuelInputs(int numFuels) {
        ArrayList<HBox> results = new ArrayList<>();
        for (int i = 0; i < numFuels; i++) {
            TextField fuelNameInput = new TextField();
            fuelNameInput.setPromptText("Fuel Name");
            TextField fuelPriceInput = new TextField();
            fuelPriceInput.setPromptText("Fuel Price");
            fuelNameInput.setPrefWidth(log.getPrefWidth()/2);
            fuelNameInput.setBackground(VisualElements.ELEMENT_BACKGROUND);
            fuelNameInput.setOnMouseEntered(x -> fuelNameInput.setBackground(VisualElements.ACTIVE_ELEMENT));
            fuelNameInput.setOnMouseExited(x -> fuelNameInput.setBackground(VisualElements.ELEMENT_BACKGROUND));
            fuelPriceInput.setPrefWidth(log.getPrefWidth()/2);
            fuelPriceInput.setBackground(VisualElements.ELEMENT_BACKGROUND);
            fuelPriceInput.setOnMouseEntered(x -> fuelPriceInput.setBackground(VisualElements.ACTIVE_ELEMENT));
            fuelPriceInput.setOnMouseExited(x -> fuelPriceInput.setBackground(VisualElements.ELEMENT_BACKGROUND));
            HBox inputs = new HBox();
            inputs.setAlignment(Pos.CENTER);
            inputs.getChildren().addAll(fuelNameInput, fuelPriceInput);
            results.add(inputs);
        }
        return results;
    }

    private String salesInfo() {
        return "This pump has completed " + salesCount + " transactions for total sales of: $" + Gas.displayPrice(totalSales);
    }

    private void handleMessage(Message message) {
        System.out.println("Incoming request: " + message.toString());
        String[] requests = message.toString().split(":");
        String messageType = requests[0];
        switch (messageType) {
            case "sale" -> completeSale(requests[1]);
        }
    }

    private void completeSale(String contents) {
        String[] info = contents.split(",");
        double gallonsSold = Double.parseDouble(info[0]);
        double pricePromised = Double.parseDouble(info[1]);
        double finalPrice = gallonsSold * pricePromised;
        totalSales += finalPrice;
        salesCount++;
        String priceString = Gas.displayPrice(finalPrice);
        updateLog("Sale completed: " + gallonsSold + " gallons for $" + priceString);
    }


}

package Devices;

import Message.Message;
import Sockets.controlPort;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CardReaderGUI extends Application {

    private int WIDTH = 250;
    private int HEIGHT = 250;
    private controlPort self;

    private Circle[] LEDs;

    private enum LEDState {
        OFF,
        ACCEPTED
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        startIO();

        Scene scene = new Scene(createCardReader(), WIDTH, HEIGHT);

        // Screen dimensions
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        double screenWidth = bounds.getWidth();
        double screenHeight = bounds.getHeight();
//        primaryStage.setX(screenWidth/15);
//        primaryStage.setY(screenHeight/8);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Card Reader");
        primaryStage.show();
    }

    private void startIO() {


        self = new controlPort("card");

    }

    /**
     * Creates CardReader visual
     *
     * @return StackPane of CardReader
     */
    private StackPane createCardReader() {
        StackPane background = new StackPane();
        background.setPadding(new Insets(18));
        background.setStyle("-fx-background-color: #2B2B2B");

        // Payment Icon
        ImageView icon = VisualElements.getImage("paymentIcon.png", 145);
        icon.setSmooth(true);
        icon.setPickOnBounds(true);
        icon.setPreserveRatio(true);
        icon.setEffect(new DropShadow(8, Color.rgb(0, 0, 0, 0.18)));
        icon.translateXProperty().set(5);
        icon.translateYProperty().set(-15);

        // Top LED bar
        LEDs = createLEDBar(4, 6);
        setLEDState(LEDState.OFF);
        HBox LEDLine = new HBox(45, LEDs);
        LEDLine.translateYProperty().set(170);
        LEDLine.setAlignment(Pos.CENTER);

        // Combine
        VBox vbox = new VBox();
        vbox.getChildren().addAll(LEDLine, icon);
        vbox.setAlignment(Pos.CENTER);

        // Clicking logic
        background.setOnMouseClicked(e -> {
            // Set LEDs to green
            setLEDState(LEDState.ACCEPTED);
            background.setDisable(true);

            String cardNumber = generateCardNumber();
            System.out.println("Generated: " + cardNumber);

            self.send(new Message(cardNumber.replaceAll(" ", "")));


            // Reset LEDs
            PauseTransition authWait = new PauseTransition(Duration.millis(700));
            authWait.setOnFinished(ev -> {
                setLEDState(LEDState.OFF);

            });
            authWait.playFromStart();
        });

        background.getChildren().add(vbox);
        StackPane.setAlignment(vbox, Pos.CENTER);
        return background;
    }

    /**
     * Creates horizontal bar of LEDs
     *
     * @param count  int, number of LEDs
     * @param radius int, size of LEDs
     * @return Circle[] array
     */
    private Circle[] createLEDBar(int count, double radius) {
        Circle[] circleArr = new Circle[count];

        for (int i = 0; i < count; i++) {
            Circle c = new Circle(radius);

            c.setStroke(Color.BLACK);
            c.setStrokeWidth(1.0);
            c.setEffect(new DropShadow(4, Color.web("#2B2B2B")));
            circleArr[i] = c;
        }

        return circleArr;
    }

    /**
     * Sets LED state, which sets LED color
     *
     * @param state enum
     */
    private void setLEDState(LEDState state) {
        Color fill = switch (state) {
            case OFF -> Color.web("#9AA0A6");
            case ACCEPTED -> Color.web("#34A853");
        };

        if (LEDs != null) {
            for (Circle c : LEDs) c.setFill(fill);
        }
    }


    /**
     * Generates 20-digit card number in format of:
     * xxxx xxxx xxxx xxxx
     *
     * @return String of card number
     */
    private String generateCardNumber() {
        String cardNumber = "";
        for (int numSet = 0; numSet < 4; numSet++) {
            for (int i = 0; i < 4; i++) {
                cardNumber += (int) (Math.random() * 10);
            }

            cardNumber += " ";
        }

        return cardNumber;
    }

}

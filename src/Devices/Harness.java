package Devices;

import Message.Message;
import Sockets.commPort;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Harness extends Application {
    private static final double WIDTH = 960, HEIGHT = 540;

    /**
     * Example usage for selecting fuel
     */
    private void testFuelSelection(commPort device) throws IOException, InterruptedException {
        device.send(new Message("t:01:s0:f0:c2:SELECT YOUR GAS TYPE"));
        Thread.sleep(40);
        device.send(new Message("b:2:m,b:3:m,t:23:s1:f1:c1:REGULAR 87"));
        Thread.sleep(40);
        device.send(new Message("b:4:m,b:5:m,t:45:s1:f1:c1:PLUS 89"));
        Thread.sleep(40);
        device.send(new Message("b:6:m,b:7:m,t:67:s1:f1:c1:PREMIUM 91"));
        Thread.sleep(40);
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN FUELING|CANCEL"));
    }

    /**
     * Example usage for receipts
     */
    private void testReceiptPrompt(commPort device) throws IOException, InterruptedException {
        device.send(new Message("t:01:s3:f2:c2:WOULD YOU LIKE A RECEIPT?"));
        Thread.sleep(40);
        device.send(new Message("b:6:x,b:7:x,t:67:s2:f2:c1:YES|NO"));

        device.send(new Message("t:23:s3:f2:c2:RECEIPT WAS SENT TO"));
        Thread.sleep(40);
        device.send(new Message("t:45:s3:f0:c1:user@example.com"));
    }

    /**
     * Example welcome screen
     */
    private void testWelcome(commPort device) throws IOException, InterruptedException {
        device.send(new Message("t:01:s3:f2:c2:WELCOME!"));
        Thread.sleep(40);
        device.send(new Message("t:45:s3:f2:c2:PLEASE TAP YOUR CARD OR PHONE TO BEGIN"));
    }

    public void start(Stage stage) {
        Display display = new Display();

        Scene scene = new Scene(display.createPumpDisplay(), WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Screen");
        stage.show();

        new Thread(() -> {
            try {
                commPort screen = new commPort("screen");

                // testWelcome(screen);
                testFuelSelection(screen);
                // testReceiptPrompt(screen);


                System.out.println("Waiting for clicks…");
                while (true) {
                    Message m = screen.get();

                    if (m != null) System.out.println("clicked on: " + m);
                    Thread.sleep(10);
                }

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);

            }
        }).start();
    }

    public static void main(String[] args) {
        launch(args);

        try {
            //Card simulation
//            commPort card = new commPort("card");
//            System.out.println("Received Card Number: " + card.get());

            //Display simulation
//            commPort display = new commPort("screen");
//            display.send(new Message(testScreenOne));
//            System.out.println("The display responded with" + display.get());
//            display.send(new Message(testScreenTwo));
//            System.out.println("The display responded with" + display.get());
//            display.send(new Message(testScreenThree));
//            System.out.println("The display responded with" + display.get());

            //Gas Server simulation
//            commPort gasServer = new commPort("gas_server");
//            gasServer.send(new Message("pump_info"));
//            System.out.println(gasServer.get());
//            gasServer.send(new Message("fuel_info"));
//            System.out.println("Gas prices: " + gasServer.get());
//            gasServer.send(new Message("complete_sale:87,3,1.245"));
//            System.out.println("The final price of the sale was: $" + gasServer.get());
//            gasServer.send(new Message("pump_info"));
//            System.out.println(gasServer.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

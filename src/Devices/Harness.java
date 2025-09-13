package Devices;

import Message.Message;
import Sockets.commPort;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Harness {

    public static void main(String[] args) {
//        specialTest();
        String device = args[0]; // Write device in arg line

        switch (device) {
            case "display" -> {
                testDisplay();
            }
            case "card" -> {
                testCard();
            }
            case "gas" -> {
                testGasServer();
            }
            case "bank" -> {
                testBankServer();
            }
            case "hose" -> {
                testHose();
            }
            case "pump" -> {
                testPump();
            }
            case "special" -> {
                specialTest();
            }
        }
//

    }

    private static void specialTest() {
        AtomicReference<String> currentState = new AtomicReference<>("off");
        System.out.println("i am starting");
        new Thread(() -> {
            try {
                commPort gasServerPort = new commPort("gas_server");
                while (true) {
                    Message message = gasServerPort.get();
                    switch (currentState.get()) {
                        case "off" -> {
                            //String message = gasServerPort.get().toString();
                            System.out.println("done blocking for message");
                            String[] messageContents = message.toString().split(":");
                            if (messageContents[0].equals("status")) {
                                if (messageContents[1].equals("on")) {
                                    System.out.println("i am on now");
                                    currentState.set("idle");
                                }
                            }
                        }
                        case "idle" -> {
                            //Message message = gasServerPort.get();
                            String[] messageContents = message.toString().split(":");
                            if (messageContents[0].equals("status")) {
                                if (messageContents[1].equals("off")) {
                                    System.out.println("i am off now");
                                    currentState.set("off");
                                }
                            } else {
                                ArrayList<Gas> fuelOptions = Gas.parseGasses(message);
                                fuelOptions.forEach(System.out::println);
                                currentState.set("ready");
                            }
                        }
                        default -> {
                            System.out.println("unknown state or ready");
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();


        new Thread(() -> {
            try {
                commPort cardPort = new commPort("card");
                while (true) {
                    Message message = cardPort.get();
                    switch (currentState.get()) {
                        case "ready" -> {
                            System.out.println("received card number: " + message);
                        }
                        default -> {
                            System.out.println("unknown state: " + message);
                        }
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void testPump() {
        try {
            commPort pump = new commPort("pump");
            commPort flow = new commPort("flow_meter");
            new Thread(() -> {
                while (true) {
                    System.out.println(flow.get());
                }
            }).start();

            while (true) {
                Thread.sleep(1000);
                pump.send(new Message("on:REG"));
                flow.send(new Message("flow"));
                Thread.sleep(10000);
                flow.send(new Message("reset"));
                flow.send(new Message("flow"));
                Thread.sleep(10000);
                pump.send(new Message("off"));
                flow.send(new Message("reset"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void testBankServer() {
        try {
            commPort bankServer = new commPort("bank");
            bankServer.send(new Message("card:12345678910111213141"));
            System.out.println("Bank server sent: " + bankServer.get());
            bankServer.send(new Message("sale:42.39"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testHose() {
        try {
            commPort hose = new commPort("hose");
            while (true) {
                System.out.println(hose.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testDisplay() {
        try {
            commPort display = new commPort("screen");

            testWelcome(display);

            while (true) {
                Message m = display.get();

                if (m == null) continue;

                System.out.println("Display responded: " + m.toString());

                switch (m.toString()) {
                    case "0" -> testWelcome(display);
                    case "1" -> testFuelSelection(display);
                    case "2" -> testPumpingScreen(display);
                    case "3" -> testSummaryScreen(display);
                    case "4" -> testReceiptPrompt(display);
                    default -> System.out.println("ERROR ERROR ERROR");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testGasServer() {
        //Gas Server simulation
        try {
            commPort gasServer = new commPort("gas_server");
            gasServer.send(new Message("sale:14.2,2.80"));
            while (true) {
                System.out.println("Gas server sent: " + gasServer.get());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testCard() {
        //Card simulation
        try {
            commPort card = new commPort("card");
            System.out.println("Received Card Number: " + card.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Example usage for selecting fuel
     */
    private static void testFuelSelection(commPort device) throws IOException {
        device.send(new Message("t:01:s0:f0:c2:SELECT YOUR GAS TYPE"));
        device.send(new Message("b:2:m,b:3:m,t:23:s1:f1:c1:REGULAR 87"));
        device.send(new Message("b:4:m,b:5:m,t:45:s1:f1:c1:PLUS 89"));
        device.send(new Message("b:6:m,b:7:m,t:67:s1:f1:c1:PREMIUM 91"));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN FUELING|CANCEL"));
    }

    /**
     * Example welcome screen
     */
    private static void testWelcome(commPort device) throws IOException {
        device.send(new Message("t:01:s0:f0:c2:WELCOME!"));
        device.send(new Message("t:45:s1:f1:c1:Please tap your credit card or phone's digital card to begin."));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN|CANCEL"));
    }

    /**
     * Example receipts screen
     */
    private static void testReceiptPrompt(commPort device) throws IOException {
        device.send(new Message("t:01:s0:f0:c2:RECEIPT WAS SENT TO"));
        device.send(new Message("t:23:s1:f1:c1:user@example.com"));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:|OK"));
    }

    /**
     * Example summary screen
     */
    private static void testSummaryScreen(commPort device) throws IOException {
        device.send(new Message("t:01:s0:f0:c2:PUMPING FINISHED"));
        device.send(new Message("t:23:s1:f1:c1:Thank you for refilling with us!"));
        device.send(new Message("t:45:s2:f1:c0:Would you like a receipt?"));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:YES|NO"));
    }

    // "꞉" is a usable colon that won't get caught by MessageReader
    private static void testPumpingScreen(commPort device) throws IOException {
        device.send(new Message("t:01:s0:f0:c2:PUMPING IN PROGRESS"));
        device.send(new Message("t:23:s2:f1:c1:Gallons꞉ " + 10));
        device.send(new Message("t:45:s2:f1:c1:Price꞉ $" + 9));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:PAUSE|EXIT"));
    }

}
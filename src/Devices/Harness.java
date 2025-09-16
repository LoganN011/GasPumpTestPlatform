package Devices;

import Devices.DisplayObjects.ScreenState;
import Message.Message;
import Sockets.commPort;
import Sockets.controlPort;
import Sockets.monitorPort;
import Sockets.statusPort;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class Harness {

    public static void main(String[] args) {
//        specialTest();
        String device = "card"; // Write device in arg line

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
        }).start();


        new Thread(() -> {

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
        }).start();
    }

    public static void testPump() {
        try {
            controlPort pump = new controlPort("pump");
            monitorPort flow = new monitorPort("flow_meter");
            new Thread(() -> {
                while (true) {
                    Message msg = flow.read();
                    if (msg != null) {
                        System.out.println(msg);
                    }

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
            statusPort hose = new statusPort("hose");
            while (true) {
                System.out.println(hose.read());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testDisplay() {
        try {
            commPort display = new commPort("screen");

            ScreenState.welcomeScreen(display);

            while (true) {
                Message m = display.get();

                if (m == null) continue;

                System.out.println("Display responded: " + m.toString());

                switch (m.toString()) {
                    case "0" -> ScreenState.welcomeScreen(display);
                    case "1" -> ScreenState.fuelSelectionScreen(display);
                    case "2" -> ScreenState.pumpingScreen(display);
                    case "3" -> ScreenState.finishScreen(display);
                    case "4" -> ScreenState.paymentDeclinedScreen(display);
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

}
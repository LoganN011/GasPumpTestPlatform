package Devices;

import Message.Message;
import Sockets.commPort;
import java.io.IOException;

public class Harness {

    public static void main(String[] args) {
        String device = args[0]; // Write device in arg line

        switch (device) {
            case "display" -> {
                testDisplay();
            }
            case "card"    -> {
                testCard();
            }
            case "gas"     -> {
                testGasServer();
            }
            case "bank"    -> {
                testBankServer();
            }
            case "hose"    -> {
                testHose();
            }
            case "pump"    -> {
                testPump();
            }
        }
    }

    public static void testPump(){
        try{
            commPort pump = new commPort("pump");
            commPort flow = new commPort("flow_meter");

            Thread.sleep(1000);
            flow.send(new Message("on"));
            Thread.sleep(10000);
            flow.send(new Message("off"));
            System.out.println(flow.get());

        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void testBankServer() {
        try{
            commPort bankServer = new commPort("bank");
            bankServer.send(new Message("card:12345678910111213141"));
            System.out.println("Bank server sent: " + bankServer.get());
            bankServer.send(new Message("sale:42.39"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testHose(){
        try {
            commPort hose = new commPort("hose");
            while(true){
                System.out.println(hose.get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testDisplay(){
        try{
            commPort display = new commPort("screen");

            testFuelSelection(display);
            while(true) {
                System.out.println("Display responded: " + display.get());
            }
//            testWelcome(display);
//            testReceiptPrompt(display);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testGasServer(){
        //Gas Server simulation
        try{
            commPort gasServer = new commPort("gas_server");
            gasServer.send(new Message("sale:14.2,2.80"));
            while(true) {
                System.out.println("Gas server sent: " + gasServer.get());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testCard(){
        //Card simulation
        try{
            commPort card = new commPort("card");
            System.out.println("Received Card Number: " + card.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Example usage for selecting fuel
     */
    private static void testFuelSelection(commPort device) throws IOException, InterruptedException {
        device.send(new Message("t:01:s0:f0:c2:SELECT YOUR GAS TYPE"));
        device.send(new Message("b:2:m,b:3:m,t:23:s1:f1:c1:REGULAR 87"));
        device.send(new Message("b:4:m,b:5:m,t:45:s1:f1:c1:PLUS 89"));
        device.send(new Message("b:6:m,b:7:m,t:67:s1:f1:c1:PREMIUM 91"));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN FUELING|CANCEL"));
    }

    /**
     * Example welcome screen
     */
    private static void testWelcome(commPort device) throws IOException, InterruptedException {
        device.send(new Message("t:01:s0:f2:c2:WELCOME!"));
        Thread.sleep(40);
        device.send(new Message("t:45:s0:f2:c2:PLEASE TAP YOUR CARD OR PHONE TO BEGIN"));
    }

    /**
     * Example usage for receipts
     */
    private static void testReceiptPrompt(commPort device) throws IOException, InterruptedException {
        device.send(new Message("t:01:s1:f2:c2:WOULD YOU LIKE A RECEIPT?"));
        Thread.sleep(40);
        device.send(new Message("b:6:x,b:7:x,t:67:s2:f2:c1:YES|NO"));

        device.send(new Message("t:23:s1:f2:c2:RECEIPT WAS SENT TO"));
        Thread.sleep(40);
        device.send(new Message("t:45:s1:f0:c1:user@example.com"));
    }

}
package Devices;

import Message.Message;
import Sockets.commPort;
import java.io.IOException;

public class Harness {

    public static void main(String[] args) {
//        testDisplay();
//        testCard();
        testGasServer();
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
            gasServer.send(new Message("pump_info"));
            System.out.println(gasServer.get());
            gasServer.send(new Message("fuel_info"));
            System.out.println("Gas prices: " + gasServer.get());
            gasServer.send(new Message("complete_sale:87,3,1.245"));
            System.out.println("The final price of the sale was: $" + gasServer.get());
            gasServer.send(new Message("pump_info"));
            System.out.println(gasServer.get());
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
//        Thread.sleep(40);
        device.send(new Message("b:2:m,b:3:m,t:23:s1:f1:c1:REGULAR 87"));
//        Thread.sleep(40);
        device.send(new Message("b:4:m,b:5:m,t:45:s1:f1:c1:PLUS 89"));
//        Thread.sleep(40);
        device.send(new Message("b:6:m,b:7:m,t:67:s1:f1:c1:PREMIUM 91"));
//        Thread.sleep(40);
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN FUELING|CANCEL"));
    }

    /**
     * Example welcome screen
     */
    private static void testWelcome(commPort device) throws IOException, InterruptedException {
        device.send(new Message("t:01:s3:f2:c2:WELCOME!"));
        Thread.sleep(40);
        device.send(new Message("t:45:s3:f2:c2:PLEASE TAP YOUR CARD OR PHONE TO BEGIN"));
    }

    /**
     * Example usage for receipts
     */
    private static void testReceiptPrompt(commPort device) throws IOException, InterruptedException {
        device.send(new Message("t:01:s3:f2:c2:WOULD YOU LIKE A RECEIPT?"));
        Thread.sleep(40);
        device.send(new Message("b:6:x,b:7:x,t:67:s2:f2:c1:YES|NO"));

        device.send(new Message("t:23:s3:f2:c2:RECEIPT WAS SENT TO"));
        Thread.sleep(40);
        device.send(new Message("t:45:s3:f0:c1:user@example.com"));
    }

}

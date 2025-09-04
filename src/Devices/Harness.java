package Devices;

import Sockets.Message;
import Sockets.commPort;
import UIHelper.MessageReader;

public class Harness {
    public static void main(String[] args) {
        //Format for messages to screen:
        //  Buttons:    b:n:t               where n is the button number, and t is x for exclusive and m for multiple
        //  Text:       t:n[n]:sx:fy:cz:m   where n[n] is the text number, x is the size to use, y is the font to use, z is the color to use, and m is the message
        //  NOTE: Text number, size, etc. are from 0-2

        String testScreenOne = "b:0:x,t:00:s0:f1:c1:Hello World";
        String testScreenTwo = "b:1:x,t:01:s1:f2:c2:Here's A Message";
        String testScreenThree = "b:1:m,b:2:m,t:23:s2:f3:c3:A Third Message";

//        MessageReader temp = new MessageReader(testScreenOne);

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

            commPort screen = new commPort("screen");

            /* FUEL TYPE SELECTION*/
            screen.send(new Message("t:01:s3:f1:c1:SELECT YOUR GAS TYPE"));
            Thread.sleep(40);
            screen.send(new Message("b:2:m,b:3:m,t:23:s2:f1:c1:REGULAR 87"));
            Thread.sleep(40);
            screen.send(new Message("b:4:m,b:5:m,t:45:s2:f1:c1:PLUS 89"));
            Thread.sleep(40);
            screen.send(new Message("b:6:m,b:7:m,t:67:s2:f1:c1:PREMIUM 91"));
            Thread.sleep(40);
            screen.send(new Message("b:8:x,b:9:x,t:89:s2:f1:c1:BEGIN FUELING|CANCEL"));

            /* RECEIPT PROMPT */
//        screen.send(new Message("t:01:s3:f2:c2:WOULD YOU LIKE A RECEIPT?"));
//        Thread.sleep(40);
//        screen.send(new Message("b:6:x,b:7:x,t:67:s2:f2:c1:YES|NO"));

            /* RECEIPT PROMPT */
//        screen.send(new Message("t:23:s3:f2:c2:RECEIPT WAS SENT TO"));
//        Thread.sleep(40);
//        screen.send(new Message("t:45:s3:f0:c1:user@example.com"));

            /* WELCOME PAGE */
//        screen.send(new Message("t:01:s3:f2:c2:WELCOME!"));
//        Thread.sleep(40);
//        screen.send(new Message("t:45:s3:f2:c2:PLEASE TAP YOUR CARD OR PHONE TO BEGIN"));

            //Gas Server simulation
            commPort gasServer = new commPort("gas_server");
            gasServer.send(new Message("request_info"));
            System.out.println(gasServer.get());
            gasServer.send(new Message("complete_sale:87,3,1.245"));
            System.out.println("The final price of the sale was :$" + gasServer.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

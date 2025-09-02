package Devices;

import Sockets.Message;
import Sockets.commPort;

public class ScreenHarness {
    public static void main(String[] args) throws Exception {
        commPort screen = new commPort("screen");

        /** FUEL TYPE SELECTION*/
        screen.send(new Message("t:01:s3:f1:c1:SELECT YOUR GAS TYPE"));
        Thread.sleep(40);
        screen.send(new Message("b:2:m,b:3:m,t:23:s2:f1:c1:REGULAR 87"));
        Thread.sleep(40);
        screen.send(new Message("b:4:m,b:5:m,t:45:s2:f1:c1:PLUS 89"));
        Thread.sleep(40);
        screen.send(new Message("b:6:m,b:7:m,t:67:s2:f1:c1:PREMIUM 91"));
        Thread.sleep(40);
        screen.send(new Message("b:8:x,b:9:x,t:89:s2:f1:c1:BEGIN FUELING|CANCEL"));

        /** RECEIPT PROMPT */
//        screen.send(new Message("t:01:s3:f2:c2:WOULD YOU LIKE A RECEIPT?"));
//        Thread.sleep(40);
//        screen.send(new Message("b:6:x,b:7:x,t:67:s2:f2:c1:YES|NO"));

        /** RECEIPT PROMPT */
//        screen.send(new Message("t:23:s3:f2:c2:RECEIPT WAS SENT TO"));
//        Thread.sleep(40);
//        screen.send(new Message("t:45:s3:f0:c1:user@example.com"));

        /** WELCOME PAGE */
//        screen.send(new Message("t:01:s3:f2:c2:WELCOME!"));
//        Thread.sleep(40);
//        screen.send(new Message("t:45:s3:f2:c2:PLEASE TAP YOUR CARD OR PHONE TO BEGIN"));

        System.out.println("Waiting for clicks…");
        while (true) {
            Message m = screen.get();
            if (m != null) System.out.println("clicked on: " + m);
            Thread.sleep(10);
        }
    }
}

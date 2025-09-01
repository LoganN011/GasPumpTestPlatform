package Devices;

import Sockets.Message;
import Sockets.commPort;
import HelpyWelpy.MessageReader;

public class Harness {
    public static void main(String[] args) {
        //Format for messages to screen:
        //  Buttons:    b:n:t               where n is the button number, and t is x for exclusive and m for multiple
        //  Text:       t:n[n]:sx:fy:cz:m   where n[n] is the text number, x is the size to use, y is the font to use, z is the color to use, and m is the message
        //  NOTE: Text number, size, etc. are from 0-2

        String testScreenOne = "b:0:x,t:00:s0:f1:c1:Hello World";
        String testScreenTwo = "b:1:x,t:01:s1:f2:c2:Here's A Message";
        String testScreenThree = "b:1:m,b:2:m,t:23:s2:f3:c3:A Third Message";

        MessageReader temp = new MessageReader(testScreenOne);

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

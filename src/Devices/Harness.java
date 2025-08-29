package Devices;

import Sockets.Message;
import Sockets.commPort;

public class Harness {
    public static void main(String[] args) {
        //Format for messages to screen:
        //  Buttons:    b:n:t               where n is the button number, and t is x for exclusive and m for multiple
        //  Text:       t:n[n]:sx:fy:cz:m   where n[n] is the text number, x is the size to use, y is the font to use, z is the color to use, and m is the message

        String testScreenOne = "b:0:x,t:0:s1:f1:c1:Hello World";
        String testScreenTwo = "b:1:x,t:01:s2:f2:c2:Heres A Message";
        String testScreenThree = "b:1:m,b:2:m,t:23:s3:f3:c3:A Third Message";

        try {
            commPort display = new commPort("screen");
            display.send(new Message(testScreenOne));
            System.out.println("The device responded with" + display.get());
            display.send(new Message(testScreenTwo));
            System.out.println("The device responded with" + display.get());
            display.send(new Message(testScreenThree));
            System.out.println("The device responded with" + display.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

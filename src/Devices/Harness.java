package Devices;

import Sockets.Server;

public class Harness {
    public static void main(String[] args) {
        //Format for messages to screen:
        //  Buttons:    b:n:t               where n is the button number, and t is x for exclusive and m for multiple
        //  Text:       t:n[n]:sx:fy:cz:m   where n[n] is the text number, x is the size to use, y is the font to use, z is the color to use, and m is the message
        //String testScreenOne = "b:1:x,t:01:s1:f1:c1:Hello World"

        //commPort screenComm = new commPort("screen");
        //screenComm.sendMessage();
        try {
            Server server = new Server("card");
            System.out.println(server.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

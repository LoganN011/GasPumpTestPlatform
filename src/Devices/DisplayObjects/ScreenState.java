package Devices.DisplayObjects;

import Message.Message;
import Sockets.commPort;

import java.io.IOException;

public class ScreenState {

    /**
     * Welcome screen (idle)
     */
    public static void welcomeScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:WELCOME!"));
        device.send(new Message("t:45:s1:f1:c1:Please tap your credit card or phone's digital card to begin."));
    }

    /**
     * Fuel selection screen
     */
    public static void fuelSelectionScreen(commPort device, Message options) {
        device.send(new Message("t:01:s0:f0:c2:SELECT YOUR GAS TYPE"));
        device.send(options);
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN FUELING|CANCEL"));
    }

    /**
     * Payment failure screen
     */
    public static void paymentDeclinedScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:PAYMENT FAILURE"));
        device.send(new Message("t:23:s1:f1:c1:Payment was declined."));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:|OK"));
    }

    /**
     * Currently pumping fuel screen
     */
    // "꞉" is a usable colon that won't get caught by MessageReader
    public static void pumpingScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:PUMPING IN PROGRESS"));
        device.send(new Message("t:23:s2:f1:c1:Gallons꞉ " + 10));
        device.send(new Message("t:45:s2:f1:c1:Price꞉ $" + 9));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:PAUSE|EXIT"));
    }

    /**
     * Finished pumping screen
     */
    public static void finishScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:PUMPING FINISHED"));
        device.send(new Message("t:23:s1:f1:c1:Thank you for refilling with us!"));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:|OK"));
    }


    /**
     * Pump unavailable
     */
    public static void pumpUnavailableScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:PUMP UNAVAILABLE"));
        device.send(new Message("t:45:s1:f1:c1:Come back another time."));
    }

    /**
     * Pump unavailable
     */
    public static void paymentAuthorizing(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:AUTHORIZING PAYMENT"));
        device.send(new Message("t:45:s1:f1:c1:Please wait..."));
    }
}

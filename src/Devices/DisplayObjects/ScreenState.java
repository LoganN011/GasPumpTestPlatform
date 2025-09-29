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
        device.send(new Message("t:45:s1:f1:c1:Tap card to begin."));
    }

    /**
     * Fuel selection screen
     */
    public static void fuelSelectionScreen(commPort device, Message options) {
        device.send(new Message("t:01:s0:f0:c2:SELECT YOUR GAS TYPE"));
//        device.send(new Message("b:2:m,b:3:m,t:23:s1:f1:c1:REGULAR 87"));
//        device.send(new Message("b:4:m,b:5:m,t:45:s1:f1:c1:PLUS 89"));
//        device.send(new Message("b:6:m,b:7:m,t:67:s1:f1:c1:PREMIUM 91"));
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
     * Payment authorizing screen
     */
    public static void paymentAuthorizing(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:AUTHORIZING PAYMENT"));
        device.send(new Message("t:45:s1:f1:c1:Please wait."));
    }

    /**
     * Currently pumping fuel screen (W LIVE TOTALS)
     * @param device
     * @param gallons
     * @param amount
     */
    public static void pumpingScreen(commPort device, double gallons, double amount) {
        device.send(new Message("t:01:s0:f0:c2:FUELING"));
        device.send(new Message(String.format("t:23:s2:f1:c1:Gallons꞉ %.3f", gallons)));
        device.send(new Message(String.format("t:45:s2:f1:c1:Price꞉ $%.2f", amount)));
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
     * Attaching nozzle / lift nozzle
     */
    public static void attachingScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:INSERT NOZZLE"));
        device.send(new Message("t:45:s1:f1:c1:Lift nozzle and select grade"));
    }

    /**
     * Nozzle detached unexpectedly
     */
    public static void detachedScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:NOZZLE REMOVED"));
        device.send(new Message("t:45:s1:f1:c1:Re-insert to resume or press DONE"));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:RESUME|DONE"));
    }

    /**
     * Paused screen
     */
    public static void pausedScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:PAUSED"));
        device.send(new Message("t:45:s1:f1:c1:Press RESUME to continue or DONE"));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:RESUME|DONE"));
    }

    /**
     * Detaching (replace nozzle)
     */
    public static void detachingScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:REPLACE NOZZLE"));
        device.send(new Message("t:45:s1:f0:c1:Please wait…"));
    }

    /**
     * Going OFF but waiting for nozzle replacement
     */
    public static void offDetachingScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:PUMP UNAVAILABLE"));
        device.send(new Message("t:45:s1:f1:c1:Replace nozzle to finish"));
    }

}

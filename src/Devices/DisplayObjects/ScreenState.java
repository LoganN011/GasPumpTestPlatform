package Devices.DisplayObjects;

import Message.Message;
import Sockets.commPort;

import java.io.IOException;

public class ScreenState {

    /**
     * Welcome screen (idle)
     */
    public static void welcomeScreen(commPort device) {
        // temporary send empty string to clear
        device.send(new Message("t:01:s0:f0:c2:WELCOME!"));
        device.send(new Message("t:23:s0:f0:c2: "));
        device.send(new Message("t:45:s1:f1:c1:Tap card to begin."));
        device.send(new Message("t:67:s0:f0:c2: "));
        device.send(new Message("t:89:s0:f0:c2: "));
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
        device.send(new Message("t:23:s0:f0:c2: "));
        device.send(new Message("t:45:s1:f1:c1:Payment was declined."));
        device.send(new Message("t:67:s0:f0:c2: "));
        device.send(new Message("b:9:x,t:89:s2:f2:c0:|OK"));
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
        device.send(new Message("t:23:s2:f1:c1: "));
        device.send(new Message("t:45:s1:f1:c1:Thank you for refilling with us!"));
        device.send(new Message("t:67:s2:f1:c1: "));
        device.send(new Message("b:9:x,t:89:s2:f2:c0:|OK"));
    }


    /**
     * Pump unavailable
     */
    public static void pumpUnavailableScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:PUMP UNAVAILABLE"));
        device.send(new Message("t:45:s1:f1:c1:Come back another time."));
    }

    /**
     * Authorize payment
     */
    public static void paymentAuthorizing(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:AUTHORIZING PAYMENT"));
        device.send(new Message("t:45:s1:f1:c1:Please wait..."));
    }

    /**
     * Attaching nozzle / lift nozzle
     */
    public static void attachingScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:ATTACH NOZZLE"));
        device.send(new Message("t:23:s0:f0:c2: "));
        device.send(new Message("t:45:s1:f1:c1:Insert nozzle into your vehicle."));
        device.send(new Message("t:67:s0:f0:c2: "));
        device.send(new Message("t:89:s0:f0:c2: "));
    }

    /**
     * Nozzle detached unexpectedly
     */
    public static void detachedScreen(commPort device, double gallons, double amount) {
        device.send(new Message("t:01:s0:f0:c2:NOZZLE REMOVED"));
        device.send(new Message(String.format("t:23:s2:f1:c1:Gallons꞉ %.3f", gallons)));
        device.send(new Message(String.format("t:45:s2:f1:c1:Price꞉ $%.2f", amount)));
        device.send(new Message("t:67:s1:f1:c1:Re-insert to resume or FINISH."));
        device.send(new Message("b:9:x,t:89:s2:f2:c0:|FINISH"));
    }

    /**
     * Paused screen
     */
    public static void pausedScreen(commPort device,  double gallons, double amount) {
        device.send(new Message("t:01:s0:f0:c2:PAUSED"));
        device.send(new Message(String.format("t:23:s2:f1:c1:Gallons꞉ %.3f", gallons)));
        device.send(new Message(String.format("t:45:s2:f1:c1:Price꞉ $%.2f", amount)));
        device.send(new Message("t:67:s1:f1:c1:Press RESUME to continue or FINISH"));
        device.send(new Message("t:89:s0:f0:c2: "));
        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:RESUME|FINISH"));
    }

    /**
     * Detaching (replace nozzle)
     */
    public static void detachingScreen(commPort device, double gallons, double amount) {
        device.send(new Message("t:01:s0:f0:c2:REMOVE NOZZLE")); //og: REPLACE NOZZLE, should be "remove" no?
        device.send(new Message(String.format("t:23:s2:f1:c1:Gallons꞉ %.3f", gallons)));
        device.send(new Message(String.format("t:45:s2:f1:c1:Price꞉ $%.2f", amount)));
        device.send(new Message("t:67:s1:f1:c1:Please return nozzle to pump."));//og: Please wait...
        device.send(new Message("t:89:s0:f0:c2: "));
    }

    /**
     * Going OFF but waiting for nozzle replacement
     */
    public static void offDetachingScreen(commPort device) {
        device.send(new Message("t:01:s0:f0:c2:PUMP UNAVAILABLE"));
        device.send(new Message("t:23:s0:f0:c2: "));
        device.send(new Message("t:45:s1:f1:c1:Replace nozzle to finish"));
        device.send(new Message("t:67:s0:f0:c2: "));
        device.send(new Message("t:89:s0:f0:c2: "));

    }

}

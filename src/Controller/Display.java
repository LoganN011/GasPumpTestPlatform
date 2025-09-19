package Controller;

import Message.Message;
import Sockets.commPort;

import static Controller.InternalState.*;

public class Display extends Thread {

    private commPort device;

    //todo: remove once displayGUI code properly removes old texts and shows all new messages independently
    private static InternalState lastState = DETACHING;

    public Display() {
        device = new commPort("screen");

        start();
    }

    @Override
    public void run() {
        //todo make it so this is not needed
        while (true) {
            System.out.println(Controller.getState());
            switch (Controller.getState()) {
                case OFF, STANDBY -> pumpUnavailable();
                case IDLE -> welcome();
                case AUTHORIZING -> authorizing();
                case SELECTION -> fuelSelect();
            }
            System.out.println("down here");
        }
    }

    //todo: remove these if statements when displayGUI is fixed
    private void pumpUnavailable() {
        if (lastState != OFF && lastState != STANDBY) {
            device.send(new Message("t:01:s0:f0:c2:Pump Currently Unavailable"));
        }
        lastState = STANDBY;
    }

    private void welcome() {
        if (lastState != IDLE) {
            device.send(new Message("t:01:s0:f0:c2:WELCOME!,t:45:s1:f1:c1:Please tap your credit card or phone's digital card to begin."));
        }
        lastState = IDLE;
    }

    private void authorizing(){
        if (lastState != AUTHORIZING) {
            device.send(new Message("t:01:s0:f0:c2:Authorizing payment...,t:45:s1:f1:c1:Please Wait"));
        }
        lastState = AUTHORIZING;
    }

    private void fuelSelect() {
        //todo show the correct screen
        if (lastState != SELECTION) {
            device.send(new Message("t:01:s0:f0:c2:Show the selection screen:45:s1:f1:c1:APPROVED!"));
        }
        lastState = SELECTION;
    }

    private void cardDeclined() {
        if (lastState != DECLINED) {
            device.send(new Message("t:01:s0:f0:c2:Show the declined screen:45:s1:f1:c1:DECLINED!"));
        }
        lastState = DECLINED;
    }

}

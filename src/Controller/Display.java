package Controller;

import Devices.Gas;
import Message.Message;
import Sockets.commPort;

import java.util.ArrayList;

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
            Message options = optionsDisplayable(Transaction.getPrices());
            device.send(options);
        }
        lastState = SELECTION;
    }

    private Message optionsDisplayable(ArrayList<Gas> options) {
        String result = "t:01:s0:f0:c2:SELECT YOUR GAS TYPE,";
        int position = 2;
        for(Gas cur: options) {
            result += String.format("b:%d:m,b:%d:m,t:%d%d:s1:f1:c1:%s %s,", position, position + 1, position, position + 1, cur.getName(), cur.getPrice());
            position += 2;
        }
        result += "b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN FUELING|CANCEL";
        //        device.send(new Message("t:01:s0:f0:c2:SELECT YOUR GAS TYPE"));
//        device.send(new Message("b:2:m,b:3:m,t:23:s1:f1:c1:REGULAR 87"));
//        device.send(new Message("b:4:m,b:5:m,t:45:s1:f1:c1:PLUS 89"));
//        device.send(new Message("b:6:m,b:7:m,t:67:s1:f1:c1:PREMIUM 91"));
//        device.send(new Message("b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN FUELING|CANCEL"));
        return new Message(result);
    }

    private void cardDeclined() {
        if (lastState != DECLINED) {
            device.send(new Message("t:01:s0:f0:c2:Show the declined screen:45:s1:f1:c1:DECLINED!"));
        }
        lastState = DECLINED;
    }

}

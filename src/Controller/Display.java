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
        while (true) {
            switch (Controller.getState()) {
                case OFF, STANDBY -> pumpUnavailable();
                case IDLE -> welcome();
                case AUTHORIZING -> authorizing();
                case SELECTION -> fuelSelect();
                //TODO add the remainder of the states
            }
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
        if(Controller.timerEnded()){
            Controller.setState(STANDBY);
        }
        lastState = AUTHORIZING;
    }

    private void fuelSelect() {
        //todo show the correct screen
        if (lastState != SELECTION) {
            Message options = optionsDisplayable(Controller.getNewPriceList());
            device.send(options);
        }
        if(Controller.timerEnded()){
            Controller.setState(STANDBY);
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
        return new Message(result);
    }

    private void cardDeclined() {
        if (lastState != DECLINED) {
            device.send(new Message("t:01:s0:f0:c2:Show the declined screen:45:s1:f1:c1:DECLINED!"));
        }
        if(Controller.timerEnded()){
            Controller.setState(STANDBY);
        }
        lastState = DECLINED;
    }

}

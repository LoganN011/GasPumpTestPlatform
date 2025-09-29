package Controller;

import Devices.DisplayGUI;
import Devices.DisplayObjects.ScreenState;
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
                case DECLINED -> cardDeclined();
                case ATTACHING -> attaching();
                case FUELING -> fueling();
                case DETACHED -> detached();
                case PAUSED -> pause();
                case DETACHING -> detaching();
                case COMPLETE -> complete();
                case OFF_DETACHING -> offDetaching();
            }

            Message m = device.get();
            if (m == null) continue;
            System.out.println("Display responded: " + m.toString());
        }
    }

    //todo: remove these if statements when displayGUI is fixed
    private void pumpUnavailable() {
        ScreenState.welcomeScreen(device);
//        if (lastState != OFF && lastState != STANDBY) {
          //  ScreenState.pumpUnavailableScreen(device);
//        }
        lastState = STANDBY;
    }

    private void welcome() {
//        if (lastState != IDLE) {
            ScreenState.welcomeScreen(device);
//        }
        lastState = IDLE;
    }

    private void authorizing(){
//        if (lastState != AUTHORIZING) {
            ScreenState.paymentAuthorizing(device);
//        }
        if(Controller.timerEnded()){
            Controller.setState(STANDBY);
        }
        lastState = AUTHORIZING;
    }

    private void fuelSelect() {
        //todo show the correct screen
//        if (lastState != SELECTION) {
            int position = 2;
            String list = "";
            ArrayList<Gas> options = Controller.getNewPriceList();
            for(Gas cur: options) {
                list += String.format("b:%d:m,b:%d:m,t:%d%d:s1:f1:c1:%s %s,", position, position + 1, position, position + 1, cur.getName(), cur.getPrice());
                position += 2;
            }

            ScreenState.fuelSelectionScreen(device, new Message(list));
//        }
        if(Controller.timerEnded()){
            Controller.setState(STANDBY);
        }
        lastState = SELECTION;

    }

    private void cardDeclined() {
//        if (lastState != DECLINED) {
            ScreenState.paymentDeclinedScreen(device);
//        }
        if(Controller.timerEnded()){
            Controller.setState(STANDBY);
        }
        lastState = DECLINED;
    }

    private void attaching() {

    }

    private void fueling() {

    }

    private void detached() {

    }

    private void pause() {

    }

    private void detaching() {
    }

    private void complete() {

    }

    private void offDetaching() {

    }

}

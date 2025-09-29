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
            ScreenState.pumpUnavailableScreen(device);
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
        int position = 2;
        String list = "";
        ArrayList<Gas> options = Controller.getNewPriceList();
        if (options != null) {
            for (Gas cur : options) {
                // Example line: "b:2:m,b:3:m,t:23:s1:f1:c1:Regular $3.59,"
                String label = String.format("%s $%.2f", cur.getName(), cur.getPrice());
                list += String.format("b:%d:m,b:%d:m,t:%d%d:s1:f1:c1:%s,", position, position + 1, position, position + 1, label);
                position += 2;
            }
        }
        ScreenState.fuelSelectionScreen(device, new Message(list));

        if (Controller.timerEnded()) {
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
        ScreenState.attachingScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
        lastState = ATTACHING;
    }

    private void fueling() {
        int gallons = Controller.getGasAmount();

        // Read selected gas & price
        Devices.Gas g = Controller.getCurrentGas();
        double pricePerGallon = (g != null ? g.getPrice() : 0.0);

        double total = gallons * pricePerGallon;

        Devices.DisplayObjects.ScreenState.pumpingScreen(device, gallons, total);

        lastState = InternalState.FUELING;
    }


    private void detached() {
        ScreenState.detachedScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
        lastState = DETACHED;
    }

    private void pause() {
        ScreenState.pausedScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
        lastState = PAUSED;
    }


    private void detaching() {
        ScreenState.detachingScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
        lastState = DETACHING;
    }


    private void complete() {
        ScreenState.finishScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
        lastState = COMPLETE;
    }

    private void offDetaching() {
        ScreenState.offDetachingScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(OFF);
        }
        lastState = OFF_DETACHING;
    }
}

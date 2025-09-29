package Controller;

import Devices.DisplayObjects.ScreenState;
import Devices.Gas;
import Message.Message;
import Sockets.commPort;

import java.util.ArrayList;

import static Controller.InternalState.*;

public class Display extends Thread {

    private final commPort device;
    private int buttonID;

    public Display() {
        device = new commPort("screen");
        start();
    }

    @Override
    public void run() {
        while (true) {

            Message m = device.get();
            if (m == null) continue;
            System.out.println("Display responded: " + m.toString());

            buttonID = Integer.parseInt(m.toString());
            Controller.handleClick(buttonID);
        }
    }


    //todo: remove these if statements when displayGUI is fixed
    public void pumpUnavailable() {
        ScreenState.pumpUnavailableScreen(device);
    }

    public void showWelcome() {
        ScreenState.welcomeScreen(device);
//        Controller.setState(AUTHORIZING);
    }

    public void showAuthorizing(){
        ScreenState.paymentAuthorizing(device);

//        if (Controller.timerEnded()){
//            Controller.setState(STANDBY);
//        }
    }

    public void showFuelSelect() {
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

    }

    public void showCardDeclined() {
//        if (lastState != DECLINED) {
            ScreenState.paymentDeclinedScreen(device);
//        }
        if(Controller.timerEnded()){
            Controller.setState(STANDBY);
        }
    }

    public void attaching() {

    }

    public void fueling() {

    }

    public void detached() {

    }

    public void pause() {

    }

    public void detaching() {
    }

    private void complete() {

    }

    private void offDetaching() {

    }

    public int getButtonID() {
        return buttonID;
    }

    public void handleClick(int buttonID) {
        switch (buttonID) {
            // "Cancel" or "Exit" or "No" or "Ok"
            case 9 -> {
                // WELCOME SCREEN
                if (Controller.getState() == IDLE) {
                    Controller.setState(STANDBY);
                    return;
                }

            }

            // "Begin Fueling" or "Pause" or "Begin" (BEGIN IS TEMPORARY) or "Yes"
            case 8 -> {

//                if (getState() == InternalState.)
            }

            // Gas Type
            //case 7, 5, 3 -> selectGas(buttonID);
            default -> {
                System.out.println("welcome");
                //displayProcess.welcome();
            }
        }
    }

}

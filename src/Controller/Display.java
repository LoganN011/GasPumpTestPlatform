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
    public void showUnavailable() {
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
        int position = 2;
        String list = "";
        ArrayList<Gas> options = Controller.getNewPriceList();
        for (Gas cur: options) {
            String label = String.format("%s $%.2f", cur.getName(), cur.getPrice());
            list += String.format("b:%d:m,b:%d:m,t:%d%d:s1:f1:c1:%s,", position, position + 1, position, position + 1, label);
            position += 2;
        }

        ScreenState.fuelSelectionScreen(device, new Message(list));

        if (Controller.timerEnded()){
            Controller.setState(STANDBY);
        }
    }

    public void showCardDeclined() {
        ScreenState.paymentDeclinedScreen(device);

        if (Controller.timerEnded()){
            Controller.setState(STANDBY);
        }
    }

    public void showAttaching() {
        ScreenState.attachingScreen(device);

//        if (Controller.timerEnded()) {
//            Controller.setState(STANDBY);
//        }
    }

    public void showFueling() {
        int gallons = Controller.getGasAmount();

        // Read selected gas & price
        Devices.Gas g = Controller.getCurrentGas();
        double pricePerGallon = (g != null ? g.getPrice() : 0.0);

        double total = gallons * pricePerGallon;

        ScreenState.pumpingScreen(device, gallons, total);
    }

    public void updateFueling(int gallons, double total) {
        ScreenState.pumpingScreen(device, gallons, total);
    }


    public void showDetached() {
        ScreenState.detachedScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
    }

    public void showPause() {
        ScreenState.pausedScreen(device);

        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
    }


    public void showDetaching() {
        ScreenState.detachingScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
    }


    public void showComplete() {
        ScreenState.finishScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
    }

    public void showOffDetaching() {
        ScreenState.offDetachingScreen(device);
        if (Controller.timerEnded()) {
            Controller.setState(OFF);
        }
    }

}

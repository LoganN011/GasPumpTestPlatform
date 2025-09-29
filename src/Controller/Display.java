package Controller;

import Devices.Gas;
import Message.Message;
import Sockets.commPort;

import javax.naming.ldap.Control;
import java.util.ArrayList;

import static Controller.InternalState.*;

public class Display {

    private static commPort device;

    //todo: remove once displayGUI code properly removes old texts and shows all new messages independently
    private static InternalState lastState = DETACHING;

    public static void start() {
        new Thread(() -> {
            device = new commPort("screen");
            while (true) {
                switch (Controller.getState()) {
                    //todo change the names of the display methods to match SRS
                    case OFF, STANDBY -> pumpUnavailable();
                    case IDLE -> welcome();
                    case AUTHORIZING -> authorizing();
                    case SELECTION -> fuelSelect();
                    case DECLINED -> cardDeclined();
                    case ATTACHING -> attachHose();
                    case FUELING -> fueling();
                    case DETACHED -> detached();
                    case PAUSED -> paused();
                    case DETACHING -> detaching();
                    case COMPLETE -> complete();
                    case OFF_DETACHING -> detaching();
                }
            }
        }).start();

    }

    private static void complete() {
        String message = "";
        message += "t:01:s0:f0:c2:PUMPING FINISHED";
        message += ",t:23:s2:f1:c1: ";
        message += ",t:45:s1:f1:c1:Thank you for refilling with us!";
        message += ",t:67:s2:f1:c1: ";
        message += ",b:9:x,t:89:s2:f2:c0:|OK";
        device.send(new Message(message));
    }

    private static void detaching() {
        device.send(new Message("t:01:s0:f0:c2:PLEASE DETACH THE HOSE"));
    }

    private static void paused() {
        String message = "";
        message += "t:01:s0:f0:c2:FUELING PAUSED";
        message += String.format(",t:23:s2:f1:c1:Gallons꞉ %d", Controller.getGasAmount());
        message += String.format(",t:45:s2:f1:c1:Price꞉ $%.2f", Controller.getCurPrice());
        message += ",t:67:s1:f1:c1:Press RESUME to continue or FINISH";
        message += ",t:89:s0:f0:c2: ";
        message += ",b:8:x,b:9:x,t:89:s2:f2:c0:RESUME|FINISH";
        device.send(new Message(message));
    }

    private static void detached() {
        String message = "";
        message += "t:01:s0:f0:c2:NOZZLE REMOVED";
        message += String.format(",t:23:s2:f1:c1:Gallons꞉ %d", Controller.getGasAmount());
        message += String.format(",t:45:s2:f1:c1:Price꞉ $%.2f", Controller.getCurPrice());
        message += ",t:67:s1:f1:c1:Re-insert to resume or FINISH.";
        message += ",b:9:x,t:89:s2:f2:c0:|FINISH";
        device.send(new Message(message));
    }


    private static void attachHose() {
        device.send(new Message("t:01:s0:f0:c2:PLEASE ATTACH THE HOSE"));
    }

    private static void fueling() {
        String message = "";
        //The current gas amount is not updated and also the price is not set
        //The display is also broken
        if (lastState != OFF && lastState != STANDBY) {
            message += "t:01:s0:f0:c2:PUMPING IN PROGRESS";
            message += String.format(",t:23:s2:f1:c1:Gallons꞉ %d", Controller.getGasAmount());
            message += String.format(",t:45:s2:f1:c1:Price꞉ $%.2f", Controller.getCurPrice());
            message += ",b:8:x,b:9:x,t:89:s2:f2:c0:PAUSE|EXIT";
            device.send(new Message(message));
        }
        lastState = ATTACHING;
    }

    //todo: remove these if statements when displayGUI is fixed
    private static void pumpUnavailable() {
        if (lastState != OFF && lastState != STANDBY) {
            device.send(new Message("t:01:s0:f0:c2:Pump Currently Unavailable"));
        }
        lastState = STANDBY;
    }

    private static void welcome() {
        if (lastState != IDLE) {
            device.send(new Message("t:01:s0:f0:c2:WELCOME!,t:45:s1:f1:c1:Please tap your credit card or phone's digital card to begin."));
        }
        lastState = IDLE;
    }

    private static void authorizing() {
        if (lastState != AUTHORIZING) {
            device.send(new Message("t:01:s0:f0:c2:Authorizing payment...,t:45:s1:f1:c1:Please Wait"));
        }
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
        lastState = AUTHORIZING;
    }

    private static void fuelSelect() {
        //todo show the correct screen
        if (lastState != SELECTION) {
            Message options = optionsDisplayable(Controller.getInUsePriceList());
            device.send(options);
        }
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
        lastState = SELECTION;
        int recentInput = -1;
        int numberSelected = -1;
        boolean begin = false;
        boolean cancel = false;
        while(true) {
            String buttonInput = device.get().toString();
            try{
                recentInput = Integer.parseInt(buttonInput);
                System.out.println("button " + recentInput + " last used");
            } catch (Exception e) {
                System.out.println("bad button selection");
                e.printStackTrace();
            }
            switch (recentInput) {
                case 3,5,7 ->  numberSelected = recentInput;
                case 8 -> {
                    System.out.println("begin pressed");
                    if(numberSelected != -1) begin = true;
                }
                case 9 -> cancel = true;
            }
            System.out.println(numberSelected);
            if (cancel) {
                //todo consider this
            }
            if(begin) {
                String options = "357";
                Controller.setCurrentGas(Controller.getInUsePriceList().get(options.indexOf("" + numberSelected)));
                Controller.setState(ATTACHING);
                System.out.println("moving on");
                break;
            }
        }

    }

    private static Message optionsDisplayable(ArrayList<Gas> options) {
        String result = "t:01:s0:f0:c2:SELECT YOUR GAS TYPE,";
        int position = 2;
        for (Gas cur : options) {
            result += String.format("b:%d:m,b:%d:m,t:%d%d:s1:f1:c1:%s %s,", position, position + 1, position, position + 1, cur.getName(), cur.getPrice());
            position += 2;
        }
        result += "b:8:x,b:9:x,t:89:s2:f2:c0:BEGIN FUELING|CANCEL";
        return new Message(result);
    }

    private static void cardDeclined() {
        if (lastState != DECLINED) {
            device.send(new Message("t:01:s0:f0:c2:Show the declined screen:45:s1:f1:c1:DECLINED!"));
        }
        if (Controller.timerEnded()) {
            Controller.setState(STANDBY);
        }
        lastState = DECLINED;
    }

}

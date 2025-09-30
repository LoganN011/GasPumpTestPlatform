package Controller;

import Devices.Gas;
import Message.Message;
import Sockets.commPort;

import java.util.ArrayList;

import static Controller.InternalState.*;

public class Display {

    private static commPort device;
    //TODO Pause and resume and multiple times break
    //TODO doing the process again breaks when selecting the begin fueling
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
        device.send(new Message(message));
        if(Controller.timerEnded()) {
            Controller.setState(IDLE);
        }
    }

    private static void detaching() {
        device.send(new Message("t:01:s0:f0:c2:PLEASE DETACH THE HOSE"));
    }

    private static void paused() {
        new Thread(()->{
            while(true){
                int buttonInput = Integer.parseInt(device.get().toString());
                System.out.println(buttonInput);
                if(buttonInput == 8) {
                    Controller.setState(FUELING);
                }
                else if (buttonInput == 9) {
                    Controller.setState(DETACHING);
                }
            }
        }).start();
        String message = "";
        message += "t:01:s0:f0:c2:FUELING PAUSED";
        message += String.format(",t:23:s2:f1:c1:Gallons꞉ %.2f", Controller.getGasAmount());
        message += String.format(",t:45:s2:f1:c1:Price꞉ $%.2f", Controller.getCurPrice());
        message += ",t:67:s1:f1:c1:Press RESUME to continue or FINISH";
        message += ",b:8:x,b:9:x,t:89:s2:f2:c0:RESUME|FINISH";
        device.send(new Message(message));
        if(Controller.timerEnded()) {
            Controller.setState(DETACHING);
        }
    }

    private static void detached() {
        new Thread(()->{
            while(true){
                int buttonInput = Integer.parseInt(device.get().toString());
                if (buttonInput == 9) {
                    Controller.setState(COMPLETE);
                    Controller.setTimer(10);
                }
            }
        }).start();
        String message = "";
        message += "t:01:s0:f0:c2:NOZZLE REMOVED";
        message += String.format(",t:23:s2:f1:c1:Gallons꞉ %.2f", Controller.getGasAmount());
        message += String.format(",t:45:s2:f1:c1:Price꞉ $%.2f", Controller.getCurPrice());
        message += ",t:67:s1:f1:c1:Re-insert to resume or FINISH.";
        message += ",b:9:x,t:89:s2:f2:c0:|FINISH";

        device.send(new Message(message));
        if(Controller.timerEnded()){
            Controller.setState(IDLE);
        }
    }


    private static void attachHose() {
        device.send(new Message("t:01:s0:f0:c2:PLEASE ATTACH THE HOSE"));
        if(Controller.timerEnded()) {
            Controller.setState(COMPLETE);
            Controller.setTimer(10);
        }
    }

    private static void fueling() {
        new Thread(()->{
            while(true){
                int buttonInput = Integer.parseInt(device.get().toString());
                if(buttonInput == 8) {
                    Controller.setState(PAUSED);
                    Controller.setTimer(10);
                }
                else if (buttonInput == 9) {
                    Controller.setState(DETACHING);
                }
            }
        }).start();
        String message = "";
        message += "t:01:s0:f0:c2:PUMPING IN PROGRESS";
        message += String.format(",t:23:s2:f1:c1:Gallons꞉ %.2f", Controller.getGasAmount());
        message += String.format(",t:45:s2:f1:c1:Price꞉ $%.2f", Controller.getCurPrice());
        message += ",b:8:x,b:9:x,t:89:s2:f2:c0:PAUSE|EXIT";
        device.send(new Message(message));
    }

    private static void pumpUnavailable() {
        device.send(new Message("t:01:s0:f0:c2:PUMP CURRENTLY UNAVAILABLE"));
    }

    private static void welcome() {
        device.send(new Message("t:01:s0:f0:c2:WELCOME!,t:45:s1:f1:c1:Please tap your credit card or phone's digital card to begin."));
    }

    private static void authorizing() {
        device.send(new Message("t:01:s0:f0:c2:WAITING FOR AUTHORIZATION,t:45:s1:f1:c1:Please wait a moment"));
        if(Controller.timerEnded()){
            Controller.setState(IDLE);
        }
    }

    private static void fuelSelect() {
        Message msg = optionsDisplayable(Controller.getInUsePriceList());
        device.send(msg);
//        new Thread(() -> {
            int recentInput = -1;
            int numberSelected = -1;
            boolean begin = false;
            boolean cancel = false;
            while (true) {
                String buttonInput = device.get().toString();
                System.out.println(buttonInput);
                Controller.setTimer(10);
                try {
                    recentInput = Integer.parseInt(buttonInput);
                    System.out.println(recentInput);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                switch (recentInput) {
                    case 3, 5, 7 -> numberSelected = recentInput;
                    case 8 -> {
                        if (numberSelected != -1) begin = true;
                    }
                    case 9 -> cancel = true;
                }
                System.out.println(numberSelected);
                if (Controller.getState() == OFF) break;
                if (cancel) {
                    //todo consider this
                }
                if (begin) {
                    String options = "357";
                    Controller.setCurrentGas(Controller.getInUsePriceList().get(options.indexOf("" + numberSelected)));
                    Controller.setState(ATTACHING);
                    System.out.println("moving on");
                    break;
                }
            }
//        }).start();
        if (Controller.timerEnded()) {
            Controller.setState(IDLE);
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
        device.send(new Message("t:01:s0:f0:c2:PAYMENT DECLINED"));
        if(Controller.timerEnded()){
            Controller.setState(IDLE);
        }
    }

}

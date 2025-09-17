package Controller;

import Message.Message;
import Sockets.commPort;

public class Display extends Thread {

    private commPort device;

    public Display() {
        device = new commPort("screen");

        start();
    }

    @Override
    public void run() {
        int counter = 0;
        while (true) {
            System.out.println(Controller.getState());
            switch (Controller.getState()) {
                case OFF, STANDBY -> {
                    if (counter++ == 0) {
                        pumpUnavailable();

                    }
                }

                case IDLE -> welcome();
            }
            System.out.println("down here");
        }
    }

    private void pumpUnavailable() {
        device.send(new Message("t:01:s0:f0:c2:Pump Currently Unavailable"));
        device.send(new Message("t:23:s0:f0:c2:more"));
    }

    private void welcome() {
        device.send(new Message("t:01:s0:f0:c2:WELCOME!"));
        System.out.println("more prints");
        //,t:45:s1:f1:c1:Please tap your credit card or phone's digital card to begin.
    }

}

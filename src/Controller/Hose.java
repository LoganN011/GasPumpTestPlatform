package Controller;

import Message.Message;
import Sockets.statusPort;

public class Hose {
    statusPort device;
    private boolean attached = false;
    private boolean full = false;
    public Hose(){
        device = new statusPort("hose"); //might want to be comm port to make it easier
    }

    /*
    Can have internal values that are used for attached/ detached and tank full
    that way it can just send tell the controller the state based on these values
     */

    public void check() {
        Message m = device.read();
        if (m == null) return;

        String s = m.toString().trim().toLowerCase();

        switch (s) {
            case "connected" -> attached = true;
            case "disconnected" -> attached = false;
            case "tank_full", "full_tank" -> full = true;
        }
    }

    public boolean isAttached(){
       return attached;
    }

    public boolean isFull(){
        return full;
    }

}

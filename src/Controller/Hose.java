package Controller;

import Sockets.commPort;
import Sockets.statusPort;

public class Hose {
    statusPort device;
    private boolean attached;
    private boolean full;
    public Hose(){
        device = new statusPort("hose"); //might want to be comm port to make it easier
        attached = false;
        full = false;
    }
    /*
    Can have internal values that are used for attached/ detached and tank full
    that way it can just send tell the controller the state based on these values
     */

    public boolean isAttached(){
        if(device.read() == null){
            return attached;
        } else if (device.read().equals("connected")||device.read().equals("tank_full")) {
            attached = true;
            return true;
        }
        attached = false;
        return false;
    }

    public boolean isFull(){
        if(device.read() == null){
            return full;
        }
        else if (device.read().equals("full_tank")){
            full = true;
            return true;
        }
        full = false;
        return false;
    }

}

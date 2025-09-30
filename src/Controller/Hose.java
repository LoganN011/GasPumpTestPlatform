package Controller;

import Message.Message;
import Sockets.monitorPort;
import Sockets.statusPort;

public class Hose {
    monitorPort device;
    private boolean attached = false;
    private boolean full = false;
    public Hose(){
        device = new monitorPort("hose"); //might want to be comm port to make it easier
    }

    public boolean isAttached(){
        if(device.read() == null){
            return attached;
        } else if (device.read().equals("connected")||device.read().equals("full_tank")) {
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
    public void pumpOn(){
        device.send(new Message("on"));
    }
    public void pumpOff(){
        device.send(new Message("off"));
    }
}

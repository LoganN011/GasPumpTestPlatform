package Controller;

import Sockets.commPort;
import Sockets.statusPort;

public class Hose {
    statusPort device;
    public Hose(){
        device = new statusPort("hose"); //might want to be comm port to make it easier
    }
    /*
    Can have internal values that are used for attached/ detached and tank full
    that way it can just send tell the controller the state based on these values
     */
}

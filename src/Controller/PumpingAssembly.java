package Controller;

import Message.Message;
import Sockets.controlPort;
import Sockets.monitorPort;

public class PumpingAssembly {
    controlPort pump; //might want to be comm port to make it easier
    monitorPort flow; //might want to be comm port to make it easier

    public PumpingAssembly(){
        pump = new controlPort("pump");
        flow = new monitorPort("flow_meter");
    }
    /*
    Some get flow method
    some method to turn on and off the pump
    some method to reset the flow meter
     */

    public void pumpOn(){
        pump.send(new Message("on"));
    }

    public void pumpOff(){
        pump.send(new Message("off"));
    }

    public void requestFlow(){ // bad name because this tells to flow meter to start telling us the flow rate
        flow.send(new Message("flow"));
    }
    //Some method to get the current flow but might combine in request flow??

    public void resetFlow(){
        flow.send(new Message("reset"));
    }

}

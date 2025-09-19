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

    public void pumpOn(String type){
        pump.send(new Message("on:"+type));
        flow.send(new Message("flow"));
    }

    public void pumpOff(){
        pump.send(new Message("off"));
    }

    public Message readFlow(){
        //might want to check here if it is null if it is then return 0 but i am not sure
        return  flow.read();
    }

    public void resetFlow(){
        flow.send(new Message("reset"));
    }

}

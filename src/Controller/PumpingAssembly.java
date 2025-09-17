package Controller;

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
}

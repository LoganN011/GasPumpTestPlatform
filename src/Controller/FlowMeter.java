package Controller;


import Message.Message;
import Sockets.monitorPort;

public class FlowMeter {
    private monitorPort flow;

    public FlowMeter() {
        flow = new monitorPort("flow_meter");
    }

    public Message readFlow(){
        //might want to check here if it is null if it is then return 0 but i am not sure
        return  flow.read();
    }

    public void resetFlow(){
        flow.send(new Message("reset"));
    }
}

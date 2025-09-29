package Controller;


import Message.Message;
import Sockets.monitorPort;

public class FlowMeter {
    private final monitorPort flow;

    public FlowMeter() {
        flow = new monitorPort("flow_meter");
    }

    public Message readFlow(){
        //might want to check here if it is null if it is then return 0 but i am not sure
        Message m = flow.read();

        if (m != null) {
            return m;
        }

        return new Message("0");
    }

    public void resetFlow(){
        flow.send(new Message("reset"));
    }
}

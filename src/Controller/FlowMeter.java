package Controller;


import Message.Message;
import Sockets.monitorPort;

public class FlowMeter {
    private final monitorPort flow;

    public FlowMeter() {
        flow = new monitorPort("flow_meter");
    }

    public int readFlow(){
        Message m = flow.read();
        if (m != null) {
            return Integer.parseInt(flow.read().toString().split(":")[0]);
        }
        return 0;
    }

    public void resetFlow(){
        flow.send(new Message("reset"));
    }
}

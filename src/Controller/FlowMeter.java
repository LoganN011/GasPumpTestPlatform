package Controller;


import Message.Message;
import Sockets.monitorPort;

public class FlowMeter {
    private final monitorPort flow;

    public FlowMeter() {
        flow = new monitorPort("flow_meter");
        flow.send(new Message("flow"));
    }

    public double readFlow() {
        flow.send(new Message("flow"));
        Message m = flow.read();
        if (m != null) {
            return Double.parseDouble(m.toString());
        }
        return 0;
    }

    public void resetFlow() {
        flow.send(new Message("reset"));
    }
}

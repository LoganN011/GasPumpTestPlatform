package Controller;

import Message.Message;
import Sockets.controlPort;

public class Pump {

    controlPort device;

    public Pump() {
        device = new controlPort("pump");
    }

    public void pumpOn(String type) {
        device.send(new Message("on:" + type));

    }

    public void pumpOff() {
        device.send(new Message("off"));
    }
}

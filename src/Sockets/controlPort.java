package Sockets;

import Message.Message;


public class controlPort extends IOPort {
    /**
     * Make a new control port (send)
     *
     * @param deviceName name of device you are connecting to/from
     */
    public controlPort(String deviceName) {
        super(deviceName);
    }

    /**
     * Send a message to the connected device
     *
     * @param message the message being sent
     */
    public void send(Message message) {
        super.send(message);
    }

}

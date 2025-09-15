package Sockets;

import Message.Message;

import java.io.IOException;


public class controlPort extends IOPort {
    /**
     * Make a new control port (send)
     *
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    public controlPort(String deviceName) throws IOException {
        super(deviceName);
    }

    /**
     * Send a message to the connected device
     *
     * @param message the message being sent
     * @throws IOException if there is a socket error this will be thrown
     */
    public void send(Message message) throws IOException {
        super.send(message);
    }

}

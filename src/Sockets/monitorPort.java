package Sockets;

import Message.Message;

public class monitorPort extends IOPort {

    /**
     * Make a new monitorPort (send and read)
     *
     * @param deviceName name of device you are connecting to/from
     */
    public monitorPort(String deviceName) {
        super(deviceName);
    }

    /**
     * Get the status of the current object
     *
     * @return the message
     */
    public Message read() {
        return super.read();
    }

    /**
     * Send a message to the connected device
     *
     * @param message the message being sent
     */
    public void send(Message message) {
        super.send(message);
    }
    //send and read


}

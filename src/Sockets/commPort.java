package Sockets;

import Message.Message;

public class commPort extends IOPort {
    /**
     * Make a new commPort(send and get)
     *
     * @param deviceName name of device you are connecting to/from
     */
    public commPort(String deviceName) {
        super(deviceName);
    }

    /**
     * Method to get the next message received that is in the queue
     *
     * @return A Message object
     */
    public Message get() {
        return super.get();
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

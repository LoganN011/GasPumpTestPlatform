package Sockets;

import Message.Message;


public class statusPort extends IOPort {

    /**
     * Make a new statusPort(read)
     *
     * @param deviceName name of device you are connecting to/from
     */
    public statusPort(String deviceName) {
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

}

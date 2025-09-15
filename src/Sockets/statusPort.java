package Sockets;

import Message.Message;

import java.io.IOException;


public class statusPort extends IOPort {

    /**
     * Make a new statusPort(read)
     *
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    public statusPort(String deviceName) throws IOException {
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

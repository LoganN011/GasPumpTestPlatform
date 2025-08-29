package Sockets;

import javax.imageio.IIOException;
import java.io.*;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

public class controlPort {
    //send
    private Socket socket;
    private ObjectOutputStream out;

    /**
     * Make a new controlPort (can only send)
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    public controlPort(String deviceName) throws IOException {
        socket = new Socket("localhost", API.portLookup(deviceName));
        out = new ObjectOutputStream(socket.getOutputStream());
    }

    /**
     * Send a message to the connected device
     * @param message the message being sent
     * @throws IOException if there is a socket error this will be thrown
     */
    public void send(Message message) throws IOException {
        out.writeObject(message);
    }
}

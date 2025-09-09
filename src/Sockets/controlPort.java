package Sockets;

import Message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class controlPort {
    //send
    private Socket socket;
    private ObjectOutputStream out;

    /**
     * Make a new controlPort (can only send)
     *
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    public controlPort(String deviceName) throws IOException {
        boolean connected = false;
        while (!connected) {
            try {
                socket = new Socket("localhost", Port.portLookup(deviceName));
                connected = true;
            }catch (Exception e){
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }
        out = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
    }

    /**
     * Send a message to the connected device
     *
     * @param message the message being sent
     * @throws IOException if there is a socket error this will be thrown
     */
    public void send(Message message) throws IOException {
        out.writeObject(message);
    }
}

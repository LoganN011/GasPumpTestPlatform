package Sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class monitorPort {
    //send and read

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private volatile Message lastMessage;

    /**
     * Make a new monitorPort (can send and read)
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    public monitorPort(String deviceName) throws IOException {
        socket = new Socket("localhost", API.portLookup(deviceName));
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());
        new Thread(() -> {
            Message msg;
            try{
                while((msg =(Message) in.readObject()) != null ){
                    lastMessage = msg;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * TODO:Right a better description for this
     * Get the status of the current object
     * @return the message
     */
    public Message read(){
        return lastMessage;
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

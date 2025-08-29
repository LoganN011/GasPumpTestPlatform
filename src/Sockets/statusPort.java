package Sockets;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;


public class statusPort {
    //read

    private Socket socket;
    private ObjectInputStream in;
    private volatile Message lastMessage;

    /**
     * Make a new statusPort (can read)
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    public statusPort(String deviceName) throws IOException {
        socket = new Socket("localhost", API.portLookup(deviceName));
        in = new ObjectInputStream(socket.getInputStream());

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

}

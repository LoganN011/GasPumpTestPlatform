package Sockets;

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class statusPort {
    //read

    private Socket socket;
    private ObjectInputStream in;
    private volatile Message lastMessage;

    /**
     * Make a new statusPort (can read)
     *
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    public statusPort(String deviceName) throws IOException {
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
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());

        new Thread(() -> {
            Message msg;
            try {
                while ((msg = (Message) in.readObject()) != null) {
                    lastMessage = msg;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Get the status of the current object
     *
     * @return the message
     */
    public Message read() {
        return lastMessage;
    }

}

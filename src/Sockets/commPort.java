package Sockets;

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class commPort {
    //send and get messages
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private BlockingQueue<Message> queue;

    /**
     * Make a new commPort (can send and get)
     *
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    public commPort(String deviceName) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(Port.portLookup(deviceName));
            socket = serverSocket.accept();
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            queue = new LinkedBlockingQueue<>();

            new Thread(() -> {
                Message msg;
                try {
                    while ((msg = (Message) in.readObject()) != null) {
                        queue.put(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (BindException e) {
            socket = new Socket("localhost", Port.portLookup(deviceName));
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
            queue = new LinkedBlockingQueue<>();

            new Thread(() -> {
                Message msg;
                try {
                    while ((msg = (Message) in.readObject()) != null) {
                        queue.put(msg);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Method to get the next message received that is in the queue
     *
     * @return A Message object
     */
    public Message get() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
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

package Sockets;

import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class IOPort {

    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private BlockingQueue<Message> queue;
    private CountDownLatch connected = new CountDownLatch(1);
    private AtomicReference<Message> lastMessage;

    /**
     * Make a new IOPort
     *
     * @param deviceName name of device you are connecting to/from
     * @throws IOException throws if the connections breaks
     */
    protected IOPort(String deviceName) throws IOException {
        lastMessage = new AtomicReference<Message>();
        try {

            ServerSocket serverSocket = new ServerSocket(portLookup(deviceName));
            new Thread(() -> {
                try {
                    socket = serverSocket.accept();
                    out = new ObjectOutputStream(socket.getOutputStream());
                    in = new ObjectInputStream(socket.getInputStream());
                    queue = new LinkedBlockingQueue<>();
                    connected.countDown();
                    new Thread(() -> {
                        Message msg;
                        try {
                            while ((msg = (Message) in.readObject()) != null) {
                                lastMessage.set(msg);
                                queue.put(msg);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (BindException e) {
            socket = new Socket("localhost", portLookup(deviceName));
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            queue = new LinkedBlockingQueue<>();
            connected.countDown();

            new Thread(() -> {
                Message msg;
                try {
                    while ((msg = (Message) in.readObject()) != null) {
                        lastMessage.set(msg);
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
    protected Message get() {
        try {
            connected.await();
            return queue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the status of the current object
     *
     * @return the message
     */
    protected Message read() {
        return lastMessage.get();
    }

    /**
     * Send a message to the connected device
     *
     * @param message the message being sent
     * @throws IOException if there is a socket error this will be thrown
     */
    protected void send(Message message) throws IOException {
        try {
            connected.await();
            out.writeObject(message);
            out.flush();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to reset the last message seen
     * and the queue of messages when moving called
     */
    public void reset(){
        lastMessage.set(null);
        queue.clear();
    }


    /**
     * A method to determine the port number given a device name, for example
     * associates the hose device to port 7150.
     * The numbering scheme is as follows:
     * 714x is for commPort devices
     * 7140 for screen, 7141 for gas_server, 7142 for bank
     * 715x is for controlPort devices
     * 7150 for hose
     * 716x is for monitor devices
     * 7160 for flow_meter
     * 717x is for statusPort devices
     * 7170 for pump
     *
     * @param deviceName the name of the device (known to the controller)
     * @return the port number associated with the given device, -1 if device name is not recognized
     */
    private static int portLookup(String deviceName) {
        return switch (deviceName.toLowerCase()) {
            //714x for commPort
            case "screen" -> 7140;
            case "gas_server" -> 7141;
            case "bank" -> 7142;
            //715x for controlPort
            case "hose" -> 7150;
            case "card" -> 7151;
            //716x for monitorPort
            case "flow_meter" -> 7160;
            //717x for statusPort
            case "pump" -> 7170;
            default -> -1;
        };
    }
}

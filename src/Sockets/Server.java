package Sockets;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Server (int port){
        try{
            serverSocket = new ServerSocket(port);
            //Need to move this accept and assigning stuff to the run method so that
            // there can be multiple connections if we want that
            socket = serverSocket.accept();
            System.out.println(socket);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        try {
            String line;
            while ((line = in.readLine()) != null) {
                handleMessage(line);
                sendMessage("I got your message :)");
            }
        } catch (IOException e) {
            System.err.println("Error reading from socket: " + e.getMessage());
        }
    }

    /**
     * Place holder method to handle a message.
     * Will want to expand on this either in main where this will be run
     * or somewhere else to know how to respond to some messages
     * @param message the message that was sent from the server
     */
    private void handleMessage(String message){
        System.out.println("Received: " + message);

    }

    public void sendMessage(String message){
        out.println(message);
    }


    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }

    public static void main(String[] args) {
        Server server = new Server(1234);
        server.start();
    }
}

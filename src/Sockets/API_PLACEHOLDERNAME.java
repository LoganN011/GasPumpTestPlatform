package Sockets;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class API_PLACEHOLDERNAME implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private Socket currentSocket;
    private BlockingQueue<String> queue;
    public API_PLACEHOLDERNAME(Socket socket) {

        this.currentSocket = socket;
        queue = new LinkedBlockingQueue<>();
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch(Exception e){
            e.printStackTrace();
        }

    }


    public void run() {
        new Thread(() -> {
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    queue.put(line);
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }).start();
        while(true){
            try{
                System.out.println(queue.take());
            }catch(Exception e){
                e.printStackTrace();
            }

        }

    }
    public void sendMessage(String message){
        out.println(message);
    }
}

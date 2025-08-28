package Sockets;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class API_NAME {
    private Socket socket;
    private ServerSocket serverSocket;
    private API_PLACEHOLDERNAME r;


    public API_NAME(int port)  {
        try{
            serverSocket = new ServerSocket(port);
            //todo while true loop on this thread prevents constructor caller from
            //  proceeding (like in Controller line 24
            while(true){
                socket = serverSocket.accept();
                 r = new API_PLACEHOLDERNAME(socket);
                Thread t =  new Thread(r);
                t.start();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public API_NAME(String host, int port)   {
        try{
            Socket socket = new Socket(host,port);
            r = new API_PLACEHOLDERNAME(socket);
            Thread t = new Thread();
            t.start();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void sendMessage(String message){
        r.sendMessage(message);
    }
}

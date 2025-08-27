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
                System.out.println("Received: " + line);
            }
        } catch (IOException e) {
            System.err.println("Error reading from socket: " + e.getMessage());
        }
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

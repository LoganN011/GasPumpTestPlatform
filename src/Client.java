import javax.imageio.IIOException;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Client(int port) {
        try {
            socket = new Socket("localHost", port );
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e ){
            e.printStackTrace();
        }

    }

    public void run(){
        while (true){
            Scanner input = new Scanner(System.in);
            System.out.println("Words here: ");
            sendMessage(input.nextLine());
        }

    }

    public void sendMessage(String message){
        out.println(message);
    }

    public void start(){
        new Thread(this).start();
    }



}

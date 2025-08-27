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
            String line;
            try {
                if ((line = in.readLine()) != null) {
                    System.out.println(line);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public void sendMessage(String message){
        out.println(message);
    }

    /**
     * Place holder method to handle a message.
     * will want this in another class to break down the incoming string and
     * do stuff with it
     * @param message the message that was sent from the server
     */
    public void handleMessage(String message){
        System.out.println(message);
    }

    public void start(){
        new Thread(this).start();
    }



}

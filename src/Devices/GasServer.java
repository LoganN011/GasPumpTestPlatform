package Devices;

import Sockets.Message;
import Sockets.commPort;

import java.util.Arrays;

import static java.lang.System.exit;
import static java.lang.System.in;

public class GasServer {
    private Gas[] fuels;
    private double totalSales;

    public static void main(String[] args) {
        //For the sake of testing, the fuels are predetermined
        Gas[] fuels = new Gas[]{new Gas("Regular",87,2.124,100), new Gas("Premium",89,2.90,100)};
        GasServer server = new GasServer(fuels);

        try{
            commPort self = new commPort("gas_server");
            while (true) {
                String response = server.handleMessage(self.get());
                self.send(new Message(response));
            }

        } catch (Exception e) {
            e.printStackTrace();
            exit(1);
        }

    }

    private GasServer(Gas[] fuels) {
        this.fuels = fuels.clone();
    }

    private String handleMessage(Message message) {
        System.out.println("Incoming request: " + message.toString());
        String[] requests = message.toString().split(":");
        String messageType = requests[0];
        return switch (messageType) {
            case "request_info" -> displayGasses();
            case "complete_sale" -> completeSale(requests[1]);
            default -> "not sure what you sent?";
        };
    }

    private String completeSale(String contents) {
        String[] info = contents.split(",");
        int gasType = Integer.parseInt(info[0]);
        double gallonsSold = Double.parseDouble(info[1]);
        double pricePromised = Double.parseDouble(info[2]);
        double finalPrice = gallonsSold * pricePromised;
        totalSales += finalPrice;

        for(int i = 0; i < fuels.length && gasType != fuels[i].getType(); i++) {
            if(gasType == fuels[i].getType()) {
                fuels[i].sellGas(gallonsSold);
            }
        }

        return Gas.displayPrice(finalPrice) + "";
    }

    private String displayGasses() {
        return Arrays.toString(fuels);
    }
}

package Devices;

import Message.Message;
import Sockets.commPort;

import java.util.Arrays;

import static java.lang.System.exit;

public class GasServer {
    private Gas[] fuels;
    private double totalSales;
    private int salesCount;
    private boolean pumpActive;

    public static void main(String[] args) {
        //For the sake of testing, the fuels are predetermined
        Gas[] fuels = new Gas[]{new Gas("Regular", 87, 2.124, 100), new Gas("Premium", 89, 2.90, 100)};
        GasServer server = new GasServer(fuels);

        try {
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
        this.totalSales = 0;
        this.salesCount = 0;
        this.pumpActive = true;
    }

    private String setPumpState(boolean newState) {
        this.pumpActive = newState;
        return "This pump is " + ((pumpActive) ? "ON" : "OFF");
    }

    private String getPumpState() {
        return "This pump is " + ((pumpActive) ? "ON" : "OFF");
    }

    private String salesInfo() {
        return "This pump has completed " + salesCount + " transactions for total sales of: $" + Gas.displayPrice(totalSales);
    }

    private String handleMessage(Message message) {
        System.out.println("Incoming request: " + message.toString());
        String[] requests = message.toString().split(":");
        String messageType = requests[0];
        return switch (messageType) {
            case "pump_info" -> getPumpState() + "\n\t" + salesInfo();
            case "disable_pump" -> setPumpState(false);
            case "enable_pump" -> setPumpState(true);
            case "fuel_info" -> displayGasses();
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

        for (int i = 0; i < fuels.length && gasType != fuels[i].getType(); i++) {
            if (gasType == fuels[i].getType()) {
                fuels[i].sellGas(gallonsSold);
            }
        }

        salesCount++;
        return Gas.displayPrice(finalPrice) + "";
    }

    private String displayGasses() {
        return Arrays.toString(fuels);
    }
}

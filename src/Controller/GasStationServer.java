package Controller;

import Devices.Gas;
import Message.Message;
import Sockets.commPort;

import java.util.ArrayList;

public class GasStationServer {

    private commPort device;

    public GasStationServer() {
        device = new commPort("gas_server");
    }

    public void waitForPower() {
        while(true) {
            String messageContents = device.get().toString();
            if(messageContents.equals("status:on")) break;
        }
    }

    public ArrayList<Gas> waitForPrices() {
        while(true) {
            Message messageContents = device.get();
            //todo what if we receive an off instead of a price list
            if(messageContents.toString().contains("price"))
                return Gas.parseGasses(messageContents);
        }
    }
}

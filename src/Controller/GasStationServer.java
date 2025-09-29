package Controller;

import Devices.Gas;
import Message.Message;
import Sockets.commPort;

import java.util.ArrayList;

public class GasStationServer {

    private commPort device;
    private boolean powerStatus;

    public GasStationServer() {
        device = new commPort("gas_server");
        powerStatus = false;

        new Thread(() -> {
            while (true) {
                handleMessage(device.get());
            }
        }).start();
    }

    private void handleMessage(Message incoming) {
        String[] contents = incoming.toString().split(":");
        if(contents[0].equals("status")) {
            powerStatus = contents[1].equals("on");
        } else if(incoming.toString().contains("price")) {
            Controller.setNewPriceList(Gas.parseGasses(incoming));
        }
    }

    public boolean checkPower() {
        return powerStatus;
    }

    //todo remove after changing
    public ArrayList<Gas> old() {
        while(true) {
            Message messageContents = device.get();
            //todo what if we receive an off instead of a price list
            if(messageContents.toString().contains("price"))
                return Gas.parseGasses(messageContents);
        }
    }
}

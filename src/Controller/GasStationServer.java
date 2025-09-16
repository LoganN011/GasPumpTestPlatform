package Controller;

import Sockets.commPort;

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
}

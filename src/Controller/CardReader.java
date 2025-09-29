package Controller;

import Sockets.commPort;

public class CardReader {

    private final commPort device;

    public CardReader() {
        device = new commPort("card");
    }

    public String readCard() {
        return device.get().toString();
    }
}

package Controller;

import Message.Message;
import Sockets.commPort;

public class CardReader {

    private final commPort device;

    public CardReader() {
        device = new commPort("card");
    }

    public String readCard() {
        Message m = device.get();

        if (m != null) {
            return m.toString();
        }

        return null;
    }
}

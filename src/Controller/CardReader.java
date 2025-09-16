package Controller;

import Sockets.commPort;

public class CardReader {

    private commPort cardGUI;

    public CardReader() {
        cardGUI = new commPort("card");
    }
}

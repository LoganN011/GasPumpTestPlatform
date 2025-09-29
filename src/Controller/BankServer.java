package Controller;

import Message.Message;
import Sockets.commPort;

public class BankServer {

    private final commPort device;

    public BankServer() {
        device = new commPort("bank");
    }

    /**
     * Asks this BankServer to authorize a payment given a card number
     *
     * @param cardNumber the card number of the requestee
     * @return true if the request is approved, false otherwise
     */
    public boolean authorize(String cardNumber) {
        device.send(new Message("card:" + cardNumber));

        String response = device.get().toString();
        return response.equals("approved");

    }
}

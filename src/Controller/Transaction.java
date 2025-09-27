package Controller;

import Devices.Gas;
import Message.Message;

import java.util.ArrayList;

import static Controller.InternalState.*;

public class Transaction {

    private static CardReader cardReader;
    private static GasStationServer gasStationServer;
    private static BankServer bankServer;

    //todo should these be atomic reference?
    // could also use some like  Collections.synchronizedList()
//    private static ArrayList<Gas> newPriceList;
//    private static String cardNumber;
//    private static ArrayList<Gas> inUsePriceList;

    public static void start() {
        cardReader = new CardReader();
        gasStationServer = new GasStationServer();
        bankServer = new BankServer();
//        newPriceList = null;
//        cardNumber = null;
//        inUsePriceList = null;

        while (true) {
            switch (Controller.getState()) {
                case OFF -> {
                    gasStationServer.waitForPower();
                    System.out.println("ive been powered on");
                    Controller.setState(STANDBY);
                }
                case STANDBY -> {
                    Controller.setNewPriceList(gasStationServer.waitForPrices());
                    System.out.println("prices received: " + Controller.newPriceListString());
                    Controller.setState(IDLE);
                }
                case IDLE -> {
                    Controller.setCardNumber(cardReader.readCard());
                    Controller.setState(AUTHORIZING);
                }
                case AUTHORIZING -> {
                    //todo continue from here...
                    boolean approved = bankServer.authorize(Controller.getCardNumber());
                    if(approved){
                        Controller.setInUsePriceList();
                        Controller.setState(SELECTION);
                        System.out.println("approved");
                    } else {
                        Controller.setState(DECLINED);
                        System.out.println("approved");
                    }
                }

            }
        }
    }

}

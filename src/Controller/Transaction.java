package Controller;

import Devices.Gas;
import Message.Message;

import java.util.ArrayList;

import static Controller.InternalState.*;

public class Transaction extends Thread {

    private CardReader cardReader;
    private GasStationServer gasStationServer;
    private BankServer bankServer;

    //todo should these be atomic reference?
    // could also use some like  Collections.synchronizedList()
    private static ArrayList<Gas> newPriceList;
    private static String cardNumber;
    private static ArrayList<Gas> inUsePriceList;

    public Transaction() {
        cardReader = new CardReader();
        gasStationServer = new GasStationServer();
        bankServer = new BankServer();
        newPriceList = null;
        cardNumber = null;
        inUsePriceList = null;

        start();
    }

    @Override
    public void run() {
        while (true) {
            switch (Controller.getState()) {
                case OFF -> {
                    gasStationServer.waitForPower();
                    System.out.println("ive been powered on");
                    Controller.setState(STANDBY);
                }
                case STANDBY -> {
                    newPriceList = gasStationServer.waitForPrices();
                    System.out.println("prices received: " + newPriceList.toString());
                    Controller.setState(IDLE);
                }
                case IDLE -> {
                    cardNumber = cardReader.readCard();
                    Controller.setState(AUTHORIZING);
                }
                case AUTHORIZING -> {
                    //todo continue from here...
                    Controller.setTimer(10);
                    boolean approved = bankServer.authorize(cardNumber);
                    if(approved){
                        inUsePriceList = newPriceList;
                        Controller.setState(SELECTION);
                        Controller.setTimer(10);
                        System.out.println("approved");
                    } else {
                        Controller.setState(DECLINED);
                        Controller.setTimer(10);
                        System.out.println("approved");
                    }
                }

            }
        }
    }

    public static ArrayList<Gas> getPrices(){
        return inUsePriceList;
    }

}

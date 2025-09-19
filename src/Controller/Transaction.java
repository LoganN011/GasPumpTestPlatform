package Controller;

import Devices.Gas;

import java.util.ArrayList;

import static Controller.InternalState.*;

public class Transaction extends Thread { //Is this not extending process anymore?

    private CardReader cardReader;
    private GasStationServer gasStationServer;
    private BankServer bankServer;

    //todo should these be atomic reference?
    private ArrayList<Gas> newPriceList;
    private String cardNumber;

    public Transaction() {
        cardReader = new CardReader();
        gasStationServer = new GasStationServer();
        bankServer = new BankServer();
        newPriceList = null;
        cardNumber = null;

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
                    boolean approved = bankServer.authorize(cardNumber);
                    if(approved){
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

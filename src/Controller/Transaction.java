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
        new Thread(() -> {
            while (true) {
                System.out.println("TRANSACTION: " + Controller.getState());

                switch (Controller.getState()) {
                    case OFF -> {
                        //gasStationServer.waitForPower();
                        System.out.println("TRANSACTION: ive been powered on");
                        Controller.setState(STANDBY);
                    }

                    case STANDBY -> {
//                        Controller.setNewPriceList(gasStationServer.waitForPrices());
                        System.out.println("TRANSACTION: prices received: " + Controller.newPriceListString());
                        Controller.setState(IDLE);
                    }

                    case IDLE -> {
                        String card = cardReader.readCard();

                        Controller.setCardNumber(card);
                        Controller.startProcess(Controller.getState());
                    }

                    case AUTHORIZING -> {
                        //todo continue from here...
                        boolean approved = bankServer.authorize(Controller.getCardNumber());
                        Controller.setTimer(10);

                        if (approved){
                            Controller.setInUsePriceList();
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
        }).start();
    }

}

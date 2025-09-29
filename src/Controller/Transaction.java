package Controller;

import java.util.concurrent.atomic.AtomicBoolean;

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
        AtomicBoolean temp = new AtomicBoolean(true);
        cardReader = new CardReader();
        gasStationServer = new GasStationServer();
        bankServer = new BankServer();
//        newPriceList = null;
//        cardNumber = null;
//        inUsePriceList = null;
        new Thread(() -> {
            while (temp.get()) {
                System.out.println("\nTRANSACTION: " + Controller.getState());

                switch (Controller.getState()) {
                    case OFF -> {
                        gasStationServer.waitForPower();
                        System.out.println("TRANSACTION: Gas Station server ON");
                        Controller.setState(STANDBY);
                    }

                    case STANDBY -> {
                        Controller.setNewPriceList(gasStationServer.waitForPrices());
                        System.out.println("TRANSACTION: Prices Received: " + Controller.newPriceListString());
                        Controller.setState(IDLE);
                    }

                    case IDLE -> {
                        String card = cardReader.readCard();

                        Controller.setCardNumber(card);
                        Controller.startProcess(Controller.getState());
                    }

                    case AUTHORIZING -> {
                        boolean approved = bankServer.authorize(Controller.getCardNumber());
                        Controller.setTimer(2); //10

                        if (approved) {
                            Controller.setInUsePriceList();
                            Controller.setState(SELECTION);
                            Controller.setTimer(10);
                            System.out.println("TRANSACTION: CC Approved");

                        } else {
                            Controller.setState(DECLINED);
                            Controller.setTimer(10);
                            System.out.println("TRANSACTION: CC Declined");
                        }
                    }

//                    case DECLINED -> {
//                        Controller.setCardNumber(null);
//                        Controller.setState(IDLE);
//                    }

                    case SELECTION -> {
                        // stop loop
                        temp.set(false);
                    }

                    default -> System.out.println("TRANSACTION: MISSING");

                }
            }
        }).start();
    }

}

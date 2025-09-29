package Controller;

import static Controller.InternalState.*;

public class Transaction {

    private static CardReader cardReader;
    private static GasStationServer gasStationServer;
    private static BankServer bankServer;


    public static void start() {
        cardReader = new CardReader();
        gasStationServer = new GasStationServer();
        bankServer = new BankServer();

        new Thread(() -> {
            while (true) {
                if(!gasStationServer.checkPower()) {
                    if(Controller.getCurPrice() != 0) {
                        System.out.println("off received during transaction, reporting transaction now");
                        gasStationServer.report();
                        bankServer.charge();
                    }
                    Controller.reset();
                }
                switch (Controller.getState()) {
                    case OFF -> {
                        while(!gasStationServer.checkPower()){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                System.out.println("here");
                                throw new RuntimeException(e);
                            }
                        }
                        //replaced with the above while loop
//                        gasStationServer.checkPower();
                        System.out.println("TRANSACTION: Gas Station server ON");
                        Controller.setState(STANDBY);
                    }
                    case STANDBY -> {
                        //commented out because the GasStationServer sets the prices
//                        Controller.setNewPriceList(gasStationServer.waitForPrices());
                        System.out.println("up");
                        while (Controller.getNewPriceList() == null) {
                            try{
                                Thread.sleep(100);
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        System.out.println("TRANSACTION: Prices Received: " + Controller.newPriceListString());
                        Controller.setState(IDLE);
                    }

                    case IDLE -> {
                        String card = cardReader.readCard();
                        Controller.setCardNumber(card);
                        Controller.setState(AUTHORIZING);
                    }
                    case AUTHORIZING -> {
                        boolean approved = bankServer.authorize(Controller.getCardNumber());

                        if (gasStationServer.checkPower() && approved) {
                            Controller.setInUsePriceList();
                            Controller.setState(SELECTION);

                            System.out.println("TRANSACTION: CC Approved");

                        } else if(gasStationServer.checkPower()) {
                            Controller.setCardNumber(null);
                            Controller.setState(DECLINED);

                            System.out.println("TRANSACTION: CC Declined");
                        }
                    }

                    default -> {

                    }

                }
            }
        }).start();
    }

}

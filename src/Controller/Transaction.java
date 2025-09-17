package Controller;

import static Controller.InternalState.*;

public class Transaction extends Thread { //Is this not extending process anymore?

    private CardReader cardReader;
    private GasStationServer gasStationServer;
    private BankServer bankServer;

    public Transaction() {
        cardReader = new CardReader();
        gasStationServer = new GasStationServer();
        bankServer = new BankServer();

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
                    //todo: save this into a variable?
                    System.out.println("i have gotten the prices: " + gasStationServer.waitForPrices().toString());
                    Controller.setState(IDLE);
                }
            }
        }
    }
}

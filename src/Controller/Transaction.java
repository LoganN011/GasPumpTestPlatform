package Controller;

import static Controller.InternalState.OFF;

public class Transaction extends Process {

    private CardReader cardReader;
    private GasStationServer gasStationServer;
    private BankServer bankServer;

    public Transaction(InternalState state) {
        super(state);
        cardReader = new CardReader();
        gasStationServer = new GasStationServer();
        bankServer = new BankServer();

        start();
    }

    @Override
    public void run() {
        switch(state) {
            case OFF -> {
                gasStationServer.waitForPower();
                System.out.println("ive been powered on");
            }
        }

        System.out.println(cardReader.readCard());
    }
}

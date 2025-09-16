package Controller;

public class Transaction extends Thread {

    private CardReader cardReader;
    private GasStationServer gasStationServer;
    private BankServer bankServer;

    public Transaction() {
        start();

        cardReader = new CardReader();
    }

    @Override
    public void run() {

    }
}

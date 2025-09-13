package Devices;

import Sockets.commPort;

import java.util.ArrayList;

/**
 * The Controller class implements the functionality of the Gas Pump Controller.
 * When run, an instance of Controller is declared, communications established,
 * and procedure executed according to the SRS.
 */

public class Controller {
    private volatile Integer cardNumber;
    private volatile ArrayList<Gas> newPriceList;
    private volatile ArrayList<Gas> inUsePriceList;
    private volatile Integer fuelType;
    private volatile State internalState;

    commPort bankServer, cardReader, display, gasServer, hose, pump;

    private enum State {
        OFF, STANDBY, IDLE, AUTHORIZING, DECLINED, SELECTION, ATTACHING, FUELING, DETACHED, PAUSED, DETACHING, COMPLETE;
    }

    private Controller() {
        cardNumber = null;
        newPriceList = null;
        inUsePriceList = null;
        fuelType = null;
        internalState = State.OFF;
    }

    public static void main(String[] args) {
        Controller self = new Controller();
        self.establishCommunications();
    }

    private void establishCommunications() {
        try {
            bankServer = new commPort("bank");
            cardReader = new commPort("card");
            display = new commPort("display");
            gasServer = new commPort("gas_server");
            hose = new commPort("hose");
            pump = new commPort("pump");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Critical Error: Connections not established");
        }
    }
}

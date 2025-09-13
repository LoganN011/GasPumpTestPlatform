package Devices;

import Message.Message;
import Sockets.commPort;

import java.util.ArrayList;

/**
 * The Controller class implements the functionality of the Gas Pump Controller.
 * When run, an instance of Controller is declared, communications established,
 * and procedure executed according to the SRS.
 */

public class Controller {
    //todo consider changing to atomicreference, it is atomic in addition to notifying threads of updates
    /**
     * The card number associated with the current active transaction
     */
    private volatile String cardNumber;
    /**
     * The most recent price list given by the gas station server
     */
    private volatile ArrayList<Gas> newPriceList;
    /**
     * The price list being used for the current transaction
     */
    private volatile ArrayList<Gas> inUsePriceList;
    /**
     * The type of the fuel chosen by the user for the current transaction
     */
    private volatile Integer fuelType;
    /**
     * The internal state of the control system, according to the SRS
     */
    private volatile State internalState;

    /**
     * The communication ports used to speak to each of the connected devices
     */
    private commPort bankServer, cardReader, display, gasServer, hose, pump;

    private enum State {
        OFF, STANDBY, IDLE, AUTHORIZING, DECLINED, SELECTION, ATTACHING,
        FUELING, DETACHED, PAUSED, DETACHING, COMPLETE;
    }

    /**
     * Initializes the state of the controller to the default state when an
     * instance is declared.
     */
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
        self.beginProtocol();

    }

    /**
     * Start the controller listening for incoming events, respond to conditions,
     * and issue actions as described in the SRS
     */
    private void beginProtocol() {
        new Thread(() ->{ //Begin listening for gas station
            while(true) {
                Message incoming = gasServer.get();
                String[] messageContents = incoming.toString().split(":");
                String messageType = messageContents[0];
                if(messageType.equals("price")) {
                    setPrices(incoming);
                    if(internalState.equals(State.IDLE)) internalState = State.STANDBY;
                }
                switch(internalState) {
                    case OFF -> {
                        if(messageType.equals("status") && messageContents[1].equals("on")) {
                            internalState = State.STANDBY;
                            System.out.println("System turned on");
                        }
                    }
                    case STANDBY -> {
                        if(newPriceList != null) {
                            //todo send the actual welcome message
//                            display.send(new Message("welcome"));
                            internalState = State.IDLE;
                        }
                    }
                    default -> {

                    }
                }
            }
        }).start();

        new Thread(() -> { //Check for card reader conditions
            while (true) {
                if(internalState.equals(State.IDLE)) {
                    Message incoming = cardReader.get();
                    setCardNumber(incoming);
                }
            }
        }).start();
    }

    /**
     * Set the card number to the one given as message
     * @param incoming the message containing the card number
     */
    private void setCardNumber(Message incoming) {
        try{
            System.out.println("Incoming card number message: " + incoming.toString());
            cardNumber = incoming.toString();
        } catch (Exception e) {
            System.out.println("Error: The message from the card number did not arrive as expected");
        }
    }

    private void setPrices(Message incoming) {
        newPriceList = Gas.parseGasses(incoming);
        newPriceList.forEach(System.out::println);
    }

    /**
     * Connect each of the devices for this instance of controller
     */
    private void establishCommunications() {
        try {
            bankServer = new commPort("bank");
            cardReader = new commPort("card");
            display = new commPort("screen");
            gasServer = new commPort("gas_server");
            hose = new commPort("hose");
            pump = new commPort("pump");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Critical Error: Connections not established");
        }
    }
}

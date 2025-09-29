package Controller;

import Devices.Gas;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Controller {

    private static AtomicReference<InternalState> internalState = new AtomicReference<>(InternalState.OFF);
    private static AtomicInteger gasAmount = new AtomicInteger(0);
    private static AtomicReference<Gas> currentGas = new AtomicReference<>();
    private static long endingTime;
    private static AtomicReference<ArrayList<Gas>> newPriceList = new AtomicReference<>();
    private static AtomicReference<ArrayList<Gas>> inUsePriceList = new AtomicReference<>();
    //todo consider deleting cardNumber variable and setter/getter
    private static AtomicReference<String> cardNumber = new AtomicReference<>();

    private static AtomicReference<Boolean> nozzleAttached = new AtomicReference(false);
    //todo move variables here

    private static Display displayProcess;

    public static void main(String[] args) {
        displayProcess = new Display();
        Transaction.start();
        Fueling.start();

        setState(getState());
//        startProcess(getState());
    }


    public static boolean isNozzleAttached() { return nozzleAttached.get(); }
    public static void setNozzleAttached(boolean attached) { nozzleAttached.set(attached); }

    public static void setCurrentGas(Gas currentGas) {
        Controller.currentGas.set(currentGas);
    }

    public static Gas getCurrentGas() {
        return currentGas.get();
    }

    public static void setGasAmount(int newGasAmount) {
        gasAmount.set(newGasAmount);
    }

    public static int getGasAmount() {
        return gasAmount.get();
    }

    public static InternalState getState(){
        return internalState.get();
    }

    public static synchronized void setState(InternalState newState) {
        internalState.set(newState);
        startProcess(getState());
    }

    public static void setTimer(int durationSeconds) {
        long now = System.currentTimeMillis();
        endingTime = now + (durationSeconds * 1000L);
    }

    public static boolean timerEnded() {
        return System.currentTimeMillis() >= endingTime;
    }

    /**
     * This method does not take any parameters, instead takes the existing newPriceList and
     * assigns it to the inUsePriceList
     */
    public static void setInUsePriceList() {
        //todo consider disallowing the setting of the inusepricelsit during fueling
        inUsePriceList.set(newPriceList.get());
    }

    public static ArrayList<Gas> getInUsePriceList() {
        return inUsePriceList.get();
    }

    public static void setNewPriceList(ArrayList<Gas> newGivenPrices){
        newPriceList.set(newGivenPrices);
    }

    public static ArrayList<Gas> getNewPriceList() {
        return newPriceList.get();
    }

    public static String newPriceListString(){
        return newPriceList.toString();
    }

    public static void setCardNumber(String newCardNumber) {
        cardNumber.set(newCardNumber);
    }

    public static String getCardNumber() {
        return cardNumber.get();
    }

    public static void startProcess(InternalState s) {
            switch (s) {
                case OFF, STANDBY -> {
                    System.out.println("MAIN: Showing Unavail");
                    displayProcess.showUnavailable();
                }

                case IDLE -> {
                    System.out.println("MAIN: Showing Welcome");
                    displayProcess.showWelcome();

                    if (getCardNumber() != null) {
                        setState(InternalState.AUTHORIZING);
                    }
                }

                case AUTHORIZING -> {
                    System.out.println("MAIN: Showing Authorizing");
                    displayProcess.showAuthorizing();
                }

                case SELECTION -> {
                    System.out.println("MAIN: Showing Selection");
                    displayProcess.showFuelSelect();
                }

                case ATTACHING -> {
                    System.out.println("MAIN: Showing Attaching");
                    displayProcess.showAttaching();
                }

                case FUELING -> {
                    System.out.println("MAIN: Showing Fueling");
                    displayProcess.showFueling();
                }
            }
    }

    public static void handleClick(int buttonID) {
        switch (buttonID) {
            case 3, 5, 7 -> {
                int index = 0;

                ArrayList<Gas> prices = getInUsePriceList();
                if (prices == null || prices.isEmpty()) return;
                if (buttonID == 5) {
                    index = 1;
                } else if (buttonID == 7) {
                    index = 2;
                }
                setCurrentGas(prices.get(index));
                System.out.println("MAIN: " + getCurrentGas().getName());
            }

            // Begin fueling
            case 8 -> {
                if (getState() == InternalState.SELECTION && getCurrentGas() != null) {
                    if (!isNozzleAttached()) {
                        System.out.println("MAIN: Attach Nozzle");
                        setState(InternalState.ATTACHING);
                        return;
                    }

                    System.out.println("MAIN: Begin Fueling");
                    setState(InternalState.FUELING);
                }
            }

            // Cancel
            case 9 -> {
                if (getState() == InternalState.SELECTION) {
                    cardNumber.set(null);
                    setState(InternalState.IDLE);
                }
            }
        }

    }


}

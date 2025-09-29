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
    //todo move variables here

    private static Display displayProcess;

    public static void main(String[] args) {
        displayProcess = new Display();
        Transaction.start();
        Fueling.start();

        setState(InternalState.IDLE);
        startProcess(getState());
    }



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

    public static void setState(InternalState newState) {
        internalState.set(newState);
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
               // case STANDBY -> display.showWelcome();
                case IDLE    -> {
                    //System.out.println("showing welcome");
                    displayProcess.showWelcome();

                    System.out.println(getCardNumber());


                    //startProcess(getState());
                }
                case SELECTION -> {
                    System.out.println("showing selection");
                    displayProcess.showFuelSelect();
                }
                case AUTHORIZING -> {
                    System.out.println("showing auth");
                    displayProcess.showAuthorizing();
                }
            }
    }

    public static void handleClick(int buttonID) {
        switch (buttonID) {
            case 8 -> {
                System.out.println(getCardNumber());
                System.out.println(getState().toString());
                if (getState() == InternalState.IDLE) {
                    System.out.println("truer");
                    setState(InternalState.AUTHORIZING);
                    startProcess(getState());
                }


            }
        }
    }


}

package Controller;

import Devices.Gas;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Controller {

    private static AtomicReference<InternalState> internalState = new AtomicReference<>(InternalState.OFF);
    private static AtomicReference<Double> gasAmount = new AtomicReference<>();
    private static AtomicReference<Gas> currentGas = new AtomicReference<>();
    private static long endingTime=Long.MAX_VALUE;
    private static AtomicReference<ArrayList<Gas>> newPriceList = new AtomicReference<>();
    private static AtomicReference<ArrayList<Gas>> inUsePriceList = new AtomicReference<>();
    //todo consider deleting cardNumber variable and setter/getter
    private static AtomicReference<String> cardNumber = new AtomicReference<>();



    public static void main(String[] args) {
        Display.start();
        Transaction.start();
        Fueling.start();

        setState(getState());
    }

    public static void setCurrentGas(Gas currentGas) {
        Controller.currentGas.set(currentGas);
    }

    public static Gas getCurrentGas() {
        return currentGas.get();
    }

    public static void setGasAmount(double newGasAmount) {
        gasAmount.set(Double.parseDouble(Gas.displayPrice(newGasAmount)));
    }

    public static double getCurPrice() {

        return (getCurrentGas() == null) ? 0 : getCurrentGas().getPrice() * getGasAmount();
    }

    public static double getGasAmount() {
        return gasAmount.get();
    }

    public static InternalState getState(){
        return internalState.get();
    }

    public static synchronized void setState(InternalState newState) {
        internalState.set(newState);
    }

    public static void setTimer(int durationSeconds) {
        long now = System.currentTimeMillis();
        endingTime = now + (durationSeconds * 1000L);
    }

    public static void reset() {
        internalState.set(InternalState.OFF);
        gasAmount.set(0.0);
        currentGas = new AtomicReference<>();
        //todo consider changing
        endingTime = Long.MAX_VALUE;
        newPriceList = new AtomicReference<>();
        inUsePriceList = new AtomicReference<>();
        cardNumber = new AtomicReference<>();

    }



    public static boolean timerEnded() {
        if(System.currentTimeMillis() >= endingTime){
            gasAmount.set(0.0);
            currentGas = new AtomicReference<>();
            endingTime = Long.MAX_VALUE;
            inUsePriceList = new AtomicReference<>();
            cardNumber = new AtomicReference<>();
            return true;
        }
        return false;
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

    }




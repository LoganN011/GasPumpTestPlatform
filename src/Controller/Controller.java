package Controller;

import Devices.Gas;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Controller {

    private static AtomicReference<InternalState> internalState = new AtomicReference<>(InternalState.OFF);
    private static AtomicInteger gasAmount = new AtomicInteger(0);
    private static AtomicReference<Gas> currentGas = new AtomicReference<>();
    private static long endingTime;
    //todo move variables here

    public static void main(String[] args) {

        //Consider changing these to regular methods not constructors
        Transaction transactionProcess = new Transaction();
        Display displayProcess = new Display();
        Fueling.start();

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

}

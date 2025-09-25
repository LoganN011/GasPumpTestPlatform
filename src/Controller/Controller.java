package Controller;

import java.util.concurrent.atomic.AtomicReference;

public class Controller {

    private static AtomicReference<InternalState> internalState = new AtomicReference<>(InternalState.OFF); //Maybe we want this atomic because of threads

    public static void main(String[] args) {

        //Consider changing these to regular methods not constructors
        Transaction transactionProcess = new Transaction();
        Display displayProcess = new Display();
        Fueling pumpingProcess = new Fueling();

    }

    public static InternalState getState(){
        return internalState.get();
    }

    public static void setState(InternalState newState) {
        internalState.set(newState);
    }

}

package Controller;

public class Controller {

    private static InternalState internalState = InternalState.OFF; //Maybe we want this atomic because of threads

    public static void main(String[] args) {

        Transaction transactionProcess = new Transaction();
        Display displayProcess = new Display();
        Pumping pumpingProcess = new Pumping();


    }

    public static InternalState getState(){
        return internalState;
    }

    public static void setState(InternalState newState) {
        internalState = newState;
    }

}

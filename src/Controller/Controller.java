package Controller;

public class Controller {

    private static InternalState internalState = InternalState.OFF;

    public static void main(String[] args) {

        Transaction transactionProcess = new Transaction();
        Display displayProcess = new Display();


    }

    public static InternalState getState(){
        return internalState;
    }

    public static void setState(InternalState newState) {
        internalState = newState;
    }

}

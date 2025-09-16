package Controller;

public class Controller {

    private static InternalState internalState = InternalState.OFF;

    public static void main(String[] args) {

        Transaction transactionProcess = new Transaction(internalState);

    }
}

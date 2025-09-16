package Controller;

public class Process extends Thread {

    public InternalState state;

    public Process(InternalState state) {
        this.state = state;
    }

    public void setState(InternalState newState) {
        state = newState;
    }

}

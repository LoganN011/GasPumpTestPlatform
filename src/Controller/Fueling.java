package Controller;

public class Fueling extends Thread{

    private Hose hose;
    private PumpingAssembly pumpingAssembly;

    public Fueling(){
        hose = new Hose();
        pumpingAssembly = new PumpingAssembly();
    }

    public void run(){
        //DO something is here based on state
    }
}

package Controller;

public class Pumping extends Thread{

    private Hose hose;
    private PumpingAssembly pumpingAssembly;

    public Pumping(){
        hose = new Hose();
        pumpingAssembly = new PumpingAssembly();
    }

    public void run(){
        //DO something is here based on state
    }
}

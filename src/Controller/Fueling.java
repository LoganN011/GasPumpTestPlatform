package Controller;

public class Fueling extends Thread{
    private Hose hose;
    private PumpingAssembly pumpingAssembly;

    public Fueling(){
        hose = new Hose();
        pumpingAssembly = new PumpingAssembly();
        start();
    }

    //Am I missing anything?
    public void run(){
        while(true){
            switch (Controller.getState()){
                case ATTACHING, DETACHED -> {
                    if(hose.isAttached()){
                        Controller.setState(InternalState.FUELING);
                    }
                }
                case FUELING -> {
                    if(hose.isFull()){
                        Controller.setState(InternalState.DETACHING);
                        pumpingAssembly.pumpOff();
                    }
                    else if (!hose.isAttached()){
                        pumpingAssembly.pumpOff();
                        Controller.setState(InternalState.DETACHED);
                    }
                    else {
                        pumpingAssembly.pumpOn("TYPE"); //Get the type somehow
                        //return flow somehow with
                        System.out.println(pumpingAssembly.readFlow());
                    }
                }
                case PAUSED -> {
                    pumpingAssembly.pumpOff();

                }
                case DETACHING -> {
                    if(!hose.isAttached()){
                        Controller.setState(InternalState.COMPLETE);
                    }
                }
                case OFF,COMPLETE -> {
                    pumpingAssembly.pumpOff();
                    pumpingAssembly.resetFlow();
                }
            }
        }
    }
}

package Controller;

public class Fueling {
    private static Hose hose;
    private static Pump pump;
    private static FlowMeter flowMeter;
    private static boolean pauseTimer = false;

    public static void start() {
        hose = new Hose();
        pump = new Pump();
        flowMeter = new FlowMeter();

        new Thread(() -> {
            while (true) {
                switch (Controller.getState()) {
                    case ATTACHING -> {
                        if (hose.isAttached()) {
                            Controller.setState(InternalState.FUELING);
                        }
                    }
                    case DETACHED -> {
                        if (hose.isAttached()) {
                            Controller.setState(InternalState.FUELING);
                        }
                    }
                    case FUELING -> {
                        if (hose.isFull()) {
                            Controller.setState(InternalState.DETACHING);
                            pump.pumpOff();
                            hose.pumpOff();
                            
                        } else if (!hose.isAttached()) {
                            pump.pumpOff();
                            hose.pumpOff();
                            Controller.setState(InternalState.DETACHED);
                            
                        } else {
                            pump.pumpOn(Controller.getCurrentGas().getName());
                            hose.pumpOn();
                            Controller.setGasAmount(flowMeter.readFlow());
                        }
                    }
                    case PAUSED -> {
                        pump.pumpOff();
                        hose.pumpOff();
                    }
                    case DETACHING -> {
                        if (!hose.isAttached()) {
                            Controller.setState(InternalState.COMPLETE);
                            
                        }
                    }
                    case OFF, COMPLETE -> {
                        pump.pumpOff();
                        hose.pumpOff();
                        flowMeter.resetFlow();
                    }
                }
            }

        }).start();
    }
}
package Controller;

public class Fueling {
    private static Hose hose = new Hose();
    private static Pump pump = new Pump();
    private static FlowMeter flowMeter  = new FlowMeter();

    public static void start() {
        new Thread(() -> {
            while (true) {
                switch (Controller.getState()) {
                    case ATTACHING, DETACHED -> {
                        if (hose.isAttached()) {
                            Controller.setState(InternalState.FUELING);
                        }
                    }
                    case FUELING -> {
                        if (hose.isFull()) {
                            Controller.setState(InternalState.DETACHING);
                            pump.pumpOff();
                        } else if (!hose.isAttached()) {
                            pump.pumpOff();
                            Controller.setState(InternalState.DETACHED);
                        } else {
                            pump.pumpOn(Controller.getCurrentGas().getName());
                            Controller.setGasAmount(Integer.parseInt(flowMeter.readFlow().toString()));
                        }
                    }
                    case PAUSED -> {
                        pump.pumpOff();

                    }
                    case DETACHING -> {
                        if (!hose.isAttached()) {
                            Controller.setState(InternalState.COMPLETE);
                        }
                    }
                    case OFF, COMPLETE -> {
                        pump.pumpOff();
                        flowMeter.resetFlow();
                    }
                }
            }

        }).start();
    }

}

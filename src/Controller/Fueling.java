package Controller;

public class Fueling {
    private static Hose hose = new Hose();
    private static Pump pump = new Pump();
    private static FlowMeter flowMeter  = new FlowMeter();

    public static void start() {
        new Thread(() -> {
            while (true) {
                switch (Controller.getState()) {
                    case ATTACHING -> {
                        if (hose.isAttached()) {
                            Controller.setState(InternalState.FUELING);
                        }
                    }
                    case DETACHED ->{
                        Controller.setTimer(10);
                        if (hose.isAttached()) {
                            Controller.setState(InternalState.FUELING);
                        }
                    }
                    case FUELING -> {
                        if (hose.isFull()) {
                            Controller.setState(InternalState.DETACHING);
                            pump.pumpOff();
                            Controller.setTimer(10);
                        } else if (!hose.isAttached()) {
                            pump.pumpOff();
                            Controller.setState(InternalState.DETACHED);
                            Controller.setTimer(10);
                        } else {
                            pump.pumpOn(Controller.getCurrentGas().getName());
                            Controller.setGasAmount(Integer.parseInt(flowMeter.readFlow().toString()));
                        }
                    }
                    case PAUSED -> {
                        pump.pumpOff();
                        Controller.setTimer(10);
                    }
                    case DETACHING -> {
                        if (!hose.isAttached()) {
                            Controller.setState(InternalState.COMPLETE);
                            Controller.setTimer(10);
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

package Controller;

public class Fueling {
    private static Hose hose;
    private static Pump pump;
    private static FlowMeter flowMeter;

    public static void start() {
        hose = new Hose();
        pump = new Pump();
        flowMeter = new FlowMeter();

        new Thread(() -> {
            while (true) {
                hose.check();
                //System.out.println("\nFUELING: " + Controller.getState());

                switch (Controller.getState()) {
                    case OFF, COMPLETE -> {
                        pump.pumpOff();
                        flowMeter.resetFlow();
                    }

                    case ATTACHING -> {
                        if (hose.isAttached()) {
                            System.out.println("FUELING: Attached");
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
                        if (Controller.getCurrentGas() == null) {
                            break;
                        }

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

                }
            }

        }).start();
    }

}

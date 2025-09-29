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
                if (Controller.getState() == InternalState.PAUSED) {
                    if (!pauseTimer) {
                        pump.pumpOff();
                        Controller.setTimer(10);
                        pauseTimer = true;
                    }

                    continue;
                }

                pauseTimer = false;
                hose.check();

                switch (Controller.getState()) {
                    case OFF, COMPLETE -> {
                        pump.pumpOff();
                        flowMeter.resetFlow();
                    }

                    case ATTACHING -> {
                        if (hose.isAttached()) {
                            System.out.println("\nFUELING: Attached");
                            Controller.setState(InternalState.FUELING);
                        }
                    }

                    case DETACHING -> {
                        if (!hose.isAttached()) {
                            System.out.println("\nFUELING: Detaching");
                            Controller.setState(InternalState.COMPLETE);
//                            Controller.setTimer(10);
                        }
                    }

                    case DETACHED ->{
                        Controller.setTimer(10);
                        if (hose.isAttached()) {
                            System.out.println("\nFUELING: Detached");
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
                            Controller.setGasAmount(flowMeter.readFlow());
                        }
                    }

                }
            }

        }).start();
    }

}

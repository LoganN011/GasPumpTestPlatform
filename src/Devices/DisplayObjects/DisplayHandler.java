package Devices.DisplayObjects;

import Message.Message;
import Devices.Display;
import Sockets.commPort;

import java.io.IOException;

public class DisplayHandler {
    private final Display display;
    private String gasType = null;
    private long timer = 0;
    private boolean isGasSelected = false;
    private boolean isLatched = false;

    // Screen states
    private Screen currentScreenState = Screen.WELCOME;
    private enum Screen {
        WELCOME, // 0
        FUEL_SELECT, // 1
        PUMPING, // 2
        FINISH, // 3
        PAYMENT_FAIL // 4
    }

    // IO
    private volatile boolean running = true;
    private volatile commPort port;

    public DisplayHandler(Display display) {
        this.display = display;
    }

    /**
     * Display communicates with DisplayHandler through sending which
     * button ID was clicked.
     * @param buttonID int
     */
    public void onButtonClick(int buttonID) throws IOException {
        display.resetTimer();
        handleInput(buttonID);
    }

    public void startIO() {
        Thread io = new Thread(() -> {
            try {
                port = new commPort("screen");
                System.out.println("Display connected");

                while (running) {
                    Message m = port.get();
                    if (m == null) continue;
                    String line = m.toString().trim();
                    if (!line.isEmpty()) display.handleInbound(line);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }, "screen-io");
        io.setDaemon(true);
        io.start();
    }

    public void stopIO() {
        running = false;
    }


    /**
     * Handles incoming inputs from commPort
     * @param buttonID incoming commPort message
     */
    private void handleInput(int buttonID) throws IOException {
        switch (buttonID) {
            // "Cancel" or "Exit" or "No" or "Ok"
            case 9 -> {
                if (currentScreenState == Screen.WELCOME) {
                    return;
                }

                if (currentScreenState == Screen.PUMPING) {
                    clearAllSelections();
                    changeToScreen(3);
                    return;

                } else if (currentScreenState == Screen.FUEL_SELECT) {
                    //cancel();
                    changeToScreen(0);
                    return;
                }

                if (currentScreenState == Screen.FINISH) {
                    clearAllSelections();
                    changeToScreen(0);
                    return;
                }

            }

            // "Begin Fueling" or "Pause" or "Begin" (BEGIN IS TEMPORARY) or "Yes"
            case 8 ->  {
                if (currentScreenState == Screen.WELCOME) {
                    changeToScreen(1);
                    return;
                }

                if (currentScreenState == Screen.FUEL_SELECT) {
                    if (startPumping()) {
                        changeToScreen(2);
                        return;
                    }


                } else if (currentScreenState == Screen.PUMPING) {
                    pausePumping();
                    return;

                }

                if (currentScreenState == Screen.FINISH) {
                    changeToScreen(0);
                    return;
                }

            }

            // Gas Type
            case 7, 5, 3 -> selectGas(buttonID);
        }
    }

    //TODO: Still needs logic
    /**
     * Begin pumping
     */
    private boolean startPumping() throws IOException {
        if (!isGasSelected) {
            return false;
        }

        isLatched = true; // hardcore temporarily
        if (!isLatched) {
            return false;
        }

        // check hose is latched or something

        return true;
    }

    private void pausePumping() {

    }

    /**
     * Selects gas and plays selection animation
     * @param buttonID int of button ID from pump display
     */
    private void selectGas(int buttonID) {
        // If user selects new gas type, then clear current selected type
        display.clearCurrentGasSelection();

        display.markSelectedGas(buttonID);
        gasType = display.getGasField(buttonID);
        System.out.println("From Display.java:" + gasType);
        isGasSelected = true;
    }

    private void clearAllSelections() {
        display.clearCurrentGasSelection();
        isGasSelected = false;
        gasType = null;
    }

    private void changeToScreen(int state) throws IOException {
        display.resetAll();
        currentScreenState = Screen.values()[state];

        port.send(new Message(String.valueOf(state)));
    }

    public void setTime(long num) {
        timer = num;
    }

    public void doTimeout() throws IOException {
        if (timer > 5 && (currentScreenState != Screen.WELCOME && currentScreenState != Screen.PUMPING)) {

            display.resetTimer();
            clearAllSelections();
            changeToScreen(0);

        } else {
            clearAllSelections();
            display.resetTimer();
        }
    }


}
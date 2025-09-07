package Devices.DisplayObjects;

import Message.Message;
import Devices.Display;
import Sockets.commPort;
import javafx.scene.layout.VBox;

public class DisplayHandler {
    private final VBox pumpDisplay;
    private final Display display;
    private String gasType = null;
    private boolean isGasSelected = false;

    // IO
    private volatile boolean running = true;
    private volatile commPort port;

    public DisplayHandler(Display display) {
        this.display = display;
        this.pumpDisplay = display.createPumpDisplay();
    }

    /**
     * Display communicates with DisplayHandler through sending which
     * button ID was clicked.
     * @param buttonID int
     */
    public void onButtonClick(int buttonID) {
        System.out.println("From DisplayHandler.java:" + buttonID);
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
    public void handleInput(int buttonID) {

        switch (buttonID) {
            case 9 -> cancel(); // "Cancel"
            case 8 -> startPumping(); // "Begin Fueling"
            case 7, 5, 3 -> selectGas(buttonID); // Gas Type

//            case 6:
//                break;
//            case 4:
//                break;
//            case 2:
//                break;
//            case 1:
//                break;
//            case 0:
//                break;

        }
    }

    /**
     * Removes current selected gas, resets pump display to initial state.
     */
    private void cancel() {
        System.out.println("From DP: Canceled");
        display.clearCurrentGasSelection();
        isGasSelected = false;
        gasType = null;
        display.createDialogBox("Canceled", "cancel");
    }

    //TODO: Still needs logic
    /**
     * Begin pumping
     */
    private void startPumping() {
        if (!isGasSelected) {
            display.createDialogBox("Please select a fuel type first.", "cancel");
            return;
        }

        display.createDialogBox("Pumping started!", "greencheck");
        // check hose is latched or something

        // something lol
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


}
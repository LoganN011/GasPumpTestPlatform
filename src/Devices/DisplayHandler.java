package Devices;

import Message.Message;
import javafx.scene.layout.VBox;

public class DisplayHandler {
    private final VBox pumpDisplay;
    private final Display display;
    private String gasType;
    private boolean isGasSelected = false;

    public DisplayHandler() {
        this.display = new Display();
        this.pumpDisplay = display.createPumpDisplay();
    }

    /**
     * Handles incoming inputs from commPort
     * @param message incoming commPort message
     */
    public void handleInput(Message message) {
        System.out.println(message);
        String[] clickSplit = message.toString().split(":");

        if (!clickSplit[0].equals("click")) return;

        switch (clickSplit[1]) {
            case "9": // "Cancel"
                break;

            case "8": // "Begin Fueling"
                startPumping();
                break;

            case "7", "5", "3" : // Gas Type
                selectGas(clickSplit[1]);
                break;

            case "6":
                break;
            case "4":
                break;
            case "2":
                break;
            case "1":
                break;
            case "0":
                break;

        }
    }

    private void startPumping() {
        if (!isGasSelected) return;

        System.out.println("yea dog we have flow");
        // check hose is latched or something

        // something lol
    }

    private void selectGas(String buttonId) {
        if (isGasSelected) return;

        gasType = display.getGasField(Integer.parseInt(buttonId));
        isGasSelected = true;
    }

    /**
     * Gets the pump display for creating initial scene
     * @return VBox pumpDisplay
     */
    public VBox getPumpDisplay() {
        return pumpDisplay;
    }


}

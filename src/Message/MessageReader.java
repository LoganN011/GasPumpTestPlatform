package Message;

import Devices.DisplayObjects.ButtonCmd;
import Devices.DisplayObjects.TextCmd;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MessageReader {
    ArrayList<ButtonCmd> buttonCollection = new ArrayList<>();
    ArrayList<TextCmd> textCollection = new ArrayList<>();
    Text finalText;
    ReadCSVs csv;

    String msgText;

    public MessageReader(String input) {
        this.csv = new ReadCSVs();
        decodeMessage(input);
    }

    /**
     * Takes in server message and depending on whether it's
     * a button or text, will create respective JavaFX button or text
     * object.
     */
    private void decodeMessage(String input) {
        String[] messageArr = input.split(",");

        for (String message: messageArr) {
            String messageType; // "b" or "t"
            String buttonID;
            int fontSizeSelect;
            int fontNameSelect;
            int colorNum;
            boolean selectType = false;

            String[] temp = message.split(":");
            messageType = temp[0];
            buttonID = temp[1];

            // button
            if (messageType.equals("b")) {

                if (temp[2].equals("m")) {
                    selectType = true;
                }

                buttonCollection.add(new ButtonCmd(Integer.parseInt(buttonID), selectType));

            // text
            } else {
                fontSizeSelect = Integer.parseInt(temp[2].substring(1));
                fontNameSelect = Integer.parseInt(temp[3].substring(1));
                colorNum = Integer.parseInt(temp[4].substring(1));
                msgText = temp[5];

                // formatted text
                finalText = createText(csv.getFont(fontNameSelect, fontSizeSelect), csv.getColor(colorNum), msgText);

                if (msgText.contains("|")) {
                    int i = msgText.indexOf('|');
                    String left = msgText.substring(0, i);
                    String right = msgText.substring(i + 1);
                    textCollection.add(new TextCmd(buttonID, fontSizeSelect, fontNameSelect, colorNum, true, left, right, null, finalText));

                } else {
                    textCollection.add(new TextCmd(buttonID, fontSizeSelect, fontNameSelect, colorNum, false, null, null, msgText, finalText));
                }
            }
        }
    }



    /**
     * Creates JavaFX text
     */
    private Text createText(Font font, Color color, String text) {
        Text newText = new Text(text);
        newText.setFont(font);
        newText.setFill(color);

        return newText;
    }

    public ArrayList<ButtonCmd> getButtons() {
        return buttonCollection;
    }

    public ArrayList<TextCmd> getTexts() {
        return textCollection;
    }

}

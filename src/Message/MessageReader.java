package Message;

import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;

public class MessageReader {
    ArrayList<Button> buttonCollection = new ArrayList<Button>();
    Text finalText;
    ReadCSVs csv;

    public MessageReader(String input) {
        this.csv = new ReadCSVs();
        decodeMessage(input);
    }

    private void decodeMessage(String input) {
        String[] messageArr = input.split(",");

        for (String message: messageArr) {
            String messageType; // "b" or "t"
            int buttonID;
            int fontSizeSelect;
            int fontNameSelect;
            int colorNum;
            String msgText;

            String[] temp = message.split(":");

            messageType = temp[0];
            buttonID = Integer.parseInt(temp[1]);

            // button
            if (messageType.equals("b")) {
                buttonCollection.add(createButton(buttonID));

            // text
            } else {
                fontSizeSelect = Integer.parseInt(temp[2].substring(1));
                fontNameSelect = Integer.parseInt(temp[3].substring(1));
                colorNum = Integer.parseInt(temp[4].substring(1));
                msgText = temp[5];

                finalText = createText(csv.getFont(fontNameSelect, fontSizeSelect), csv.getColor(colorNum), msgText);
            }
        }


    }

    private Button createButton(int id) {
        return new Button(Integer.toString(id));
    }

    private Text createText(Font font, Color color, String text) {
        Text newText = new Text(text);
        newText.setFont(font);
        newText.setFill(color);

        return newText;
    }

    public Text getText() {
        return finalText;
    }

    public ArrayList<Button> getButtons() {
        return buttonCollection;
    }


}

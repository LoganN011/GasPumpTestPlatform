package Message;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCSVs {

    ArrayList<String> fontNameArrayList = new ArrayList<>();
    ArrayList<Integer> fontSizeArrayList = new ArrayList<>();
    ArrayList<Color> colorArrayList = new ArrayList<>();
    private final String filePath = "resources/fonts-colors/";

    public ReadCSVs() {
        readFontNameCSV();
        readFontSizesCSV();
        readColorsCSV();
    }

    /**
     * Reads CSV of font names
     */
    private void readFontNameCSV() {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath + "fonts.csv"))) {
            while ((line = br.readLine()) != null) {
                fontNameArrayList.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads CSV of font sizes
     */
    private void readFontSizesCSV() {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath + "font_sizes.csv"))) {
            while ((line = br.readLine()) != null) {
                fontSizeArrayList.add(Integer.parseInt(line));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads CSV of colors (hex values)
     */
    private void readColorsCSV() {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath + "colors.csv"))) {
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(",");

                colorArrayList.add(Color.web(temp[1].trim()));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns font based on font name and size
     */
    public Font getFont(int fontNumSelect, int fontSizeSelect) {
        String fontName = fontNameArrayList.get(fontNumSelect);
        int fontSize = fontSizeArrayList.get(fontSizeSelect);
        FontWeight fw = FontWeight.NORMAL;

        // Bold "Begin Fueling | Cancel"
        if (fontNumSelect == 2) fw = FontWeight.BOLD;

        return Font.font(fontName, fw, fontSize);
    }

    /**
     * Gets color at specific index
     */
    public Color getColor(int colorNum) {
        return colorArrayList.get(colorNum);
    }
}

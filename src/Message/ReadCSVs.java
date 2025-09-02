package Message;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadCSVs {

    ArrayList<String> fontNameArrayList = new ArrayList<>();
    ArrayList<Integer> fontSizeArrayList = new ArrayList<>();
    ArrayList<Color> colorArrayList = new ArrayList<>();

    public ReadCSVs() {
        readFontNameCSV();
        readFontSizesCSV();
        readColorsCSV();
    }

    private void readFontNameCSV() {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader("resources/" + "fonts.csv"))) {
            while ((line = br.readLine()) != null) {
                fontNameArrayList.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFontSizesCSV() {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader("resources/" + "font_sizes.csv"))) {
            while ((line = br.readLine()) != null) {
                fontSizeArrayList.add(Integer.parseInt(line));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readColorsCSV() {
        String line;

        try (BufferedReader br = new BufferedReader(new FileReader("resources/" + "colors.csv"))) {
            while ((line = br.readLine()) != null) {
                String[] temp = line.split(",");

                colorArrayList.add(Color.web(temp[1].trim()));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Font getFont(int fontNumSelect, int fontSizeSelect) {
        String fontName = fontNameArrayList.get(fontNumSelect);
        int fontSize = fontSizeArrayList.get(fontSizeSelect);

        return Font.font(fontName, fontSize);
    }

    public Color getColor(int colorNum) {
        return colorArrayList.get(colorNum);
    }
}

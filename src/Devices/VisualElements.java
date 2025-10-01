package Devices;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class VisualElements {
    public static final Color BACKGROUND = Color.color(231 / 255.0, 249 / 255.0, 255 / 255.0);
    public static final Color ELEMENTS = Color.color(198 / 255.0, 237 / 255.0, 251 / 255.0);
    public static final Color BORDERS = Color.color(32 / 255.0, 106 / 255.0, 131 / 255.0);
    public static final Color ACTIVE_COLOR = Color.color(66 / 255.0, 178 / 255.0, 215 / 255.0);
    public static final Border THIN_BORDER = new Border(new BorderStroke(BORDERS, BorderStrokeStyle.SOLID, new CornerRadii(5), BorderStroke.THIN));
    public static final Border THICK_BORDER = new Border(new BorderStroke(BORDERS, BorderStrokeStyle.SOLID, new CornerRadii(10), BorderStroke.MEDIUM));
    public static final Background ROOT_BACKGROUND = new Background(new BackgroundFill(BACKGROUND, null, null));
    public static final Background ELEMENT_BACKGROUND = new Background(new BackgroundFill(ELEMENTS, null, null));
    public static final Background ACTIVE_ELEMENT = new Background(new BackgroundFill(ACTIVE_COLOR, null, null));


    /**
     * Gets ImageView
     *
     * @param fileName String, name of file including extension
     * @param size     int, pixel size of image
     * @return ImageView
     */
    public static ImageView getImage(String fileName, int size) {
        FileInputStream inputStream;

        try {
            inputStream = new FileInputStream("resources/images/" + fileName);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        ImageView imgView = new ImageView(new Image(inputStream));

        imgView.setPreserveRatio(true);
        imgView.setFitWidth(size);

        return imgView;
    }

    public static Image getImage(String fileName) {
        FileInputStream inputStream;

        try {
            inputStream = new FileInputStream("resources/images/" + fileName);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new Image(inputStream);
    }
}

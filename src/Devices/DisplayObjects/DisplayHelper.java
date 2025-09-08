package Devices.DisplayObjects;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class DisplayHelper {

    /**
     * Helper: Sets fade in/fade out animation.
     *
     * @param ms Duration in milliseconds
     * @param sp StackPane
     * @param from Animation "from" in seconds
     * @param to Animation "to" in seconds
     * @return FadeTransition
     */
    public static FadeTransition setFadeTransition(int ms, StackPane sp, int from, int to) {
        FadeTransition fade = new FadeTransition(Duration.millis(ms), sp);
        fade.setFromValue(from);
        fade.setToValue(to);

        return fade;
    }

    /**
     * Helper: Sets slide in/slide out transition.
     *
     * @param ms Duration in milliseconds
     * @param sp StackPane
     * @param fromY From y-value
     * @param toY To y-value
     * @return TranslateTransition
     */
    public static TranslateTransition setSlideTransition(int ms, StackPane sp, int fromY, int toY) {
        TranslateTransition slide = new TranslateTransition(Duration.millis(ms), sp);
        slide.setFromY(fromY);
        slide.setToY(toY);

        return slide;
    }

    /**
     * Gets a JavaFX-compatible image.
     * @param fileName name of image including file extension
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

    /**
     * Selected gas pop animation.
     */
    public static void playPop(Node node) {
        // Pop in, then settle down
        ScaleTransition st1 = new ScaleTransition(Duration.millis(90), node);
        st1.setFromX(1.0);
        st1.setFromY(1.0);

        st1.setToX(1.06);
        st1.setToY(1.06);

        ScaleTransition st2 = new ScaleTransition(Duration.millis(110), node);
        st2.setFromX(1.06);
        st2.setFromY(1.06);

        st2.setToX(1.0);
        st2.setToY(1.0);

        new SequentialTransition(st1, st2).play();
    }
}

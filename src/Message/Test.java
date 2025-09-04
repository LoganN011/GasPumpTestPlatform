package Message;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
public class Test extends Application {

    public void start(Stage s) {
        VBox vbox = new VBox(10);
        String balls = "b:0:x,t:00:s0:f0:c0:Hello World";
        // String balls = "b:1:x,t:01:s1:f1:c1:Here's A Message";
        // String balls = "b:1:m,b:2:m,t:23:s1:f2:c2:A Third Message";

        MessageReader mr = new MessageReader(balls);
        vbox.getChildren().add(mr.getText());
        vbox.getChildren().addAll(mr.getButtons());

        Scene sc = new Scene(vbox, 200, 200);

        s.setScene(sc);
        s.show();
    }

    public static void main(String args[]) {
        launch(args);
    }
}

package Devices;

import Devices.DisplayObjects.ButtonCmd;
import Devices.DisplayObjects.TextCmd;
import Message.Message;
import Message.MessageReader;
import Sockets.commPort;
import javafx.application.Platform;

import java.io.IOException;
import java.util.List;
import java.util.function.Consumer;

/**
 * Orchestrates IO and delegates parsed commands to Display.
 * Use handleInput(...) to feed "b:..." / "t:..." frames (from sockets or tests).
 */
public class DisplayHandler {

    private final Display display;
    private commPort port;
    private volatile boolean running;

    public DisplayHandler(Display display) {
        this.display = display;

        // UI → backend: forward click events as "click:n"
        Consumer<String> clickOut = line -> {
            try {
                if (port != null) port.send(new Message(line));
            } catch (IOException e) {
                display.logLater("send failed: " + e.getMessage());
            }
        };
        this.display.setOnClick(clickOut);
    }

    /** Parse a single protocol line and render it. Safe to call from any thread. */
    public void handleInput(String line) {
        if (line == null || line.isBlank()) return;

        // Let MessageReader turn the line into shared DTOs
        MessageReader mr = new MessageReader(line);
        List<ButtonCmd> buttons = mr.getButtons();
        List<TextCmd>   texts   = mr.getTexts();

        Platform.runLater(() -> {
            if (buttons != null && !buttons.isEmpty()) display.renderButtons(buttons);
            if (texts   != null && !texts.isEmpty())   display.renderTexts(texts);
        });
    }

    /** Start background IO on "screen" port. Each inbound message calls handleInput. */
    public void startIO() {
        running = true;
        Thread t = new Thread(() -> {
            try {
                port = new commPort("screen");
                display.logLater("connected");
                while (running) {
                    Message m = port.get();
                    if (m == null) continue;
                    String line = String.valueOf(m).trim();
                    if (!line.isEmpty()) handleInput(line);
                }
            } catch (IOException e) {
                display.logLater("connect failed: " + e.getMessage());
            } catch (Exception e) {
                display.logLater("io error: " + e.getMessage());
            }
        }, "screen-io");
        t.setDaemon(true);
        t.start();
    }

    public void stopIO() {
        running = false;
        // if (port != null) try { port.close(); } catch (Exception ignore) {}
    }
}

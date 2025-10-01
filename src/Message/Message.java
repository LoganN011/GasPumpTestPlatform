package Message;

import java.io.Serializable;

public class Message implements Serializable {
    String message;

    /**
     * Make a new message
     *
     * @param message the contents of a message
     */
    public Message(String message) {
        this.message = message;
    }

    /**
     * Get a string of the message
     *
     * @return the string representation of a message
     */
    public String toString() {
        return message;
    }

    public boolean equals(String other) {
        return message.equals(other);
    }

    public boolean equals(Message other) {
        return this.message.equals(other.message);
    }
}

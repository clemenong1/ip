package bob.command;

/**
 * Represents Bob's response for the GUI.
 * Encapsulates both the message to display and whether it indicates an error.
 */
public class GuiResponse {
    private final String message;
    private final boolean isError;

    private GuiResponse(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    /**
     * Creates a normal (success) response.
     */
    public static GuiResponse success(String message) {
        return new GuiResponse(message, false);
    }

    /**
     * Creates an error response.
     */
    public static GuiResponse error(String message) {
        return new GuiResponse(message, true);
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return isError;
    }
}

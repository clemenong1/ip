package bob.gui;

import bob.Bob;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    private static final int MIN_INPUT_ROWS = 1;
    private static final int MAX_INPUT_ROWS = 10;
    private static final int CHARS_PER_WRAPPED_LINE = 45;

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextArea userInput;
    @FXML
    private Button sendButton;

    private Bob bob;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/ComputingCat.png"));
    private Image bobImage = new Image(this.getClass().getResourceAsStream("/images/BobPic.png"));

    /**
     * Initializes the main window. Binds scroll pane, shows welcome message,
     * and configures the input area to expand vertically with content.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // Show welcome message
        dialogContainer.getChildren().add(
            DialogBox.getBobDialog("Hello! I'm Bob\nWhat can I do for you?", bobImage)
        );

        // Expand input area vertically as user types
        userInput.textProperty().addListener((obs, oldVal, newVal) -> updateInputHeight());
        userInput.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ENTER && !e.isShiftDown()) {
                e.consume();
                handleUserInput();
            }
        });
    }

    private void updateInputHeight() {
        String text = userInput.getText();
        int newlineCount = text.isEmpty() ? 0 : text.split("\n", -1).length - 1;
        int maxLineLength = 0;
        for (String line : text.split("\n", -1)) {
            maxLineLength = Math.max(maxLineLength, line.length());
        }
        int wrappedRows = maxLineLength > 0
                ? (int) Math.ceil((double) maxLineLength / CHARS_PER_WRAPPED_LINE)
                : 1;
        int totalRows = Math.max(1, newlineCount + wrappedRows);
        userInput.setPrefRowCount(Math.min(MAX_INPUT_ROWS, Math.max(MIN_INPUT_ROWS, totalRows)));
    }

    /** Injects the Bob instance */
    public void setBob(Bob b) {
        bob = b;
    }

    /**
     * Creates two dialog boxes, one echoing user input and the other containing Bob's reply
     * and then appends them to the dialog container. Clears the user input after processing.
     */
    @FXML
    private void handleUserInput() {
        assert bob != null : "Bob instance must be set via setBob() before handling input";
        String input = userInput.getText().trim();
        var response = bob.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBobDialog(response.getMessage(), bobImage, response.isError())
        );
        userInput.clear();
        updateInputHeight(); // Reset to minimum height after clearing
    }
}

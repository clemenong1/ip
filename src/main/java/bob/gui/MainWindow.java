package bob.gui;

import bob.Bob;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the main GUI.
 */
public class MainWindow extends AnchorPane {
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox dialogContainer;
    @FXML
    private TextField userInput;
    @FXML
    private Button sendButton;

    private Bob bob;

    private Image userImage = new Image(this.getClass().getResourceAsStream("/images/ComputingCat.png"));
    private Image bobImage = new Image(this.getClass().getResourceAsStream("/images/BobPic.png"));

    /**
     * Initializes the main window. Binds scroll pane and shows welcome message.
     */
    @FXML
    public void initialize() {
        scrollPane.vvalueProperty().bind(dialogContainer.heightProperty());
        // Show welcome message
        dialogContainer.getChildren().add(
            DialogBox.getBobDialog("Hello! I'm Bob\nWhat can I do for you?", bobImage)
        );
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
        String input = userInput.getText();
        var response = bob.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getBobDialog(response.getMessage(), bobImage, response.isError())
        );
        userInput.clear();
    }
}

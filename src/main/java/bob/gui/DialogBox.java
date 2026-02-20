package bob.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * A custom control representing a dialog box consisting of a label and an image view.
 */
public class DialogBox extends HBox {
    private static final String BUBBLE_STYLE =
            "-fx-background-color: #e8e8e8; -fx-background-radius: 10; -fx-padding: 10;";
    private static final String ERROR_STYLE =
            "-fx-background-color: #ffcccc; -fx-background-radius: 10; -fx-padding: 10;";

    private Label text;
    private ImageView displayPicture;

    /**
     * Creates a dialog box with the given text and image.
     *
     * @param s Text to display.
     * @param i Image to display.
     */
    public DialogBox(String s, Image i) {
        this(s, i, false);
    }

    /**
     * Creates a dialog box with the given text, image, and optional error styling.
     *
     * @param s Text to display.
     * @param i Image to display.
     * @param isError If true, the bubble is styled in red to indicate an error.
     */
    public DialogBox(String s, Image i, boolean isError) {
        text = new Label(s);
        text.setWrapText(true);
        text.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(text, Priority.ALWAYS);
        text.setStyle(isError ? ERROR_STYLE : BUBBLE_STYLE);

        displayPicture = new ImageView(i);
        displayPicture.setFitWidth(100.0);
        displayPicture.setFitHeight(100.0);

        this.setAlignment(Pos.TOP_RIGHT);
        this.getChildren().addAll(text, displayPicture);
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        FXCollections.reverse(tmp);
        this.getChildren().setAll(tmp);
    }

    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img);
    }

    public static DialogBox getBobDialog(String text, Image img) {
        return getBobDialog(text, img, false);
    }

    /**
     * Creates a Bob dialog box with optional error styling.
     *
     * @param text Text to display.
     * @param img Bob's avatar image.
     * @param isError If true, the bubble is styled in red to indicate an error.
     * @return A dialog box for Bob's response.
     */
    public static DialogBox getBobDialog(String text, Image img, boolean isError) {
        var db = new DialogBox(text, img, isError);
        db.flip();
        return db;
    }
}

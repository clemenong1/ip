package bob.gui;

import java.io.IOException;

import bob.Bob;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * A GUI for Bob using FXML.
 */
public class Main extends Application {

    private Bob bob = new Bob("data/bob.txt");

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            scene.getStylesheets().add(Main.class.getResource("/view/compact-textarea.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("Bob Chatbot");
            fxmlLoader.<MainWindow>getController().setBob(bob);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

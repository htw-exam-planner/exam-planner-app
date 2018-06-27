import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class ExamPlanner extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("example.fxml"));

            Scene scene = new Scene(root, 300, 275);

            primaryStage.setTitle("Prüfungsplaner");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Anwendung konnte nicht geladen werden");
            alert.showAndWait();
            System.exit(1);
        }
    }
}

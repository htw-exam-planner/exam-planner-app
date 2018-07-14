package admin.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.stage.Stage;

import java.io.IOException;

public class MainController {
    @FXML
    MenuBar menuBar;

    /**
     * Loads the setup view to generate Appointments and views
     * @param event the event causing the method to be called
     */
    public void loadSetup(ActionEvent event){
        try {
            Parent setupViewParent = FXMLLoader.load(getClass().getResource("/admin/views/SetupView.fxml"));

            Scene setupScene = new Scene(setupViewParent);

            Stage window = (Stage) menuBar.getScene().getWindow();

            window.setScene(setupScene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Generierungsansicht konnte nicht geladen werden");
            alert.showAndWait();
            System.exit(1);
        }
    }
}

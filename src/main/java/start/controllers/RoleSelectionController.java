package start.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class RoleSelectionController {

    /**
     * Loads the Admin View
     * @param event
     */
    public void loadAdmin(ActionEvent event){

    }

    /**
     * Loads the GroupSelectionView for logging in as a group
     * @param event
     */
    public void loadStudent(ActionEvent event){
        try {
            Parent groupSelectionParent = FXMLLoader.load(getClass().getResource("/student/views/GroupSelectionView.fxml"));

            Scene groupSelectionScene = new Scene(groupSelectionParent);

            Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();

            window.setScene(groupSelectionScene);
            window.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Studentenansicht konnte nicht geladen werden");
            alert.showAndWait();
            System.exit(1);
        }

    }
}

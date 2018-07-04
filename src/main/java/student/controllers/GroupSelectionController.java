package student.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import models.Group;

import java.io.IOException;
import java.util.List;

public class GroupSelectionController {
    @FXML ChoiceBox groupsChoice;

    /**
     * Puts the group names into the groupsChoice choice box
     */
    @FXML
    public void initialize(){
        List<Group> groups = null;
        try {
            groups = Group.all();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,
                    "Fehler bei Datenbankverbindung");
            alert.showAndWait();

            System.exit(2);
        }

        groupsChoice.setItems(FXCollections.observableArrayList(groups));
    }

    /**
     * Loads the Students AppoinentmentView when group logs in
     */
    public void loadAppointments(ActionEvent event){
        //TODO: load
    }
}

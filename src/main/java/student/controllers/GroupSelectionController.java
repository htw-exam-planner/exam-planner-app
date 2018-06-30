package student.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import models.Group;

import java.util.List;

public class GroupSelectionController {
    @FXML ChoiceBox groupsChoice;

    /**
     * Puts the group names into the groupsChoice choice box
     */
    @FXML
    public void initialize(){
        List<Group> groups = Group.all();

        groupsChoice.setItems(FXCollections.observableArrayList(groups));
    }

    /**
     * Loads the Students AppoinentmentView when group logs in
     */
    public void loadAppointments(ActionEvent event){
        //TODO: load
    }
}

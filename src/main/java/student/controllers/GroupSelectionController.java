package student.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import models.Group;

import java.io.IOException;
import java.util.List;

public class GroupSelectionController {
    @FXML
    ChoiceBox groupsChoice;

    /**
     * Puts the group names into the groupsChoice choice box
     */
    @FXML
    public void initialize() {
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
     *
     * @param event The Event
     */
    public void loadAppointments(ActionEvent event) {
        try {
            StudentAppointmentController controller = new StudentAppointmentController((Group) groupsChoice.getValue());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/student/views/StudentAppointmentView.fxml"));
            loader.setController(controller);
            Parent appointmentViewParent = loader.load();
            Scene appointmentViewScene = new Scene(appointmentViewParent);
            Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
            window.setScene(appointmentViewScene);
            window.show();

        } catch (IOException e) {
            System.err.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Terminansicht konnte nicht geladen werden");
            alert.showAndWait();
            System.exit(1);
        }
    }
}

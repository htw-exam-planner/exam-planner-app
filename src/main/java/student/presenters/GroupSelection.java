package student.presenters;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import models.Group;

import java.util.List;

public class GroupSelection {
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
        StudentAppointments controller = new StudentAppointments((Group) groupsChoice.getValue());
        Scene appointmentViewScene = new Scene(controller);
        Stage window = (Stage) ((Node) event.getSource()).getScene().getWindow();
        window.setScene(appointmentViewScene);
        window.show();
    }
}

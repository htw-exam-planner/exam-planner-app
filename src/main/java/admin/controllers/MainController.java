package admin.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import shared.controller.Appointments;

import java.io.IOException;

public class MainController {
    @FXML
    MenuBar menuBar;

    @FXML
    AdminAppointments appointmentsView;

    @FXML
    AdminGroups groupView;

    /**
     * Adds event handlers to updates of the Groups and Appointments updating the other view
     */
    @FXML
    public void initialize(){
        appointmentsView.addEventHandler(Appointments.APPOINTMENTS_UPDATED, event -> groupView.showGroups());
        groupView.addEventHandler(AdminGroups.GROUPS_UPDATED, event -> appointmentsView.showAppointments());
    }

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

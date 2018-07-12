package admin.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Appointment;
import models.Group;
import models.InvalidAppointmentStateException;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

public class SetupController {
    @FXML
    DatePicker startDatePicker;

    @FXML
    TextField groupsField;

    public void generate(ActionEvent event){
        try {
            LocalDate startDate = startDatePicker.getValue();
            int groupCount = Integer.parseInt(groupsField.getText());

            Appointment.generate(startDate);
            Group.generate(groupCount);

            Parent adminViewParent = FXMLLoader.load(getClass().getResource("/admin/views/MainView.fxml"));

            Scene adminScene = new Scene(adminViewParent);

            Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();

            window.setScene(adminScene);
            window.show();
        }
        catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Administratoransicht konnte nicht geladen werden");
            alert.showAndWait();
            System.exit(1);
        }
        catch (InvalidAppointmentStateException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Startdatum muss ein Montag sein");
            alert.showAndWait();
        }
        catch (IllegalArgumentException e){
            Alert alert = new Alert(Alert.AlertType.ERROR, "Anzahl Gruppen muss mindestens 1 sein");
            alert.showAndWait();
        }
        catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Verbindung zur Datenbank fehlgeschlagen");
            alert.showAndWait();
        }
    }
}

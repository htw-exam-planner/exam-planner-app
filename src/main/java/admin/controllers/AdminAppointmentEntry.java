package admin.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Appointment;
import models.Group;
import shared.controller.AppointmentEntry;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class AdminAppointmentEntry extends AppointmentEntry {

    @FXML
    Label daylabel;

    @FXML
    Label notelabel;

    @FXML
    Label statelabel;

    /**
     * Creates an StudentAppointmentEntry component
     *
     * @param appointment The Appointment to display
     */
    public AdminAppointmentEntry(Appointment appointment) {
        super(appointment);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/admin/views/AdminAppointmentEntry.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Displays/Updates the Appointment information in the component
     */
    public void paint() {
        daylabel.setText(getDateString(appointment.getDate()));
        notelabel.setText(appointment.getNote());
        statelabel.setText(getStateString());

        statelabel.setTextFill(getStateColor());
    }

    /**
     * Event Handler for editing the appointmennt
     *
     * @param event The click Event
     */
    public void edit(ActionEvent event) {
        EditDialog editDialog = new EditDialog(appointment);
        editDialog.addEventHandler(EditDialog.APPOINTMENT_UPDATED, e -> emitAppointmentUpdated());
        Stage dialog = new Stage();
        dialog.setScene(new Scene(editDialog));
        dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.showAndWait();
    }
}

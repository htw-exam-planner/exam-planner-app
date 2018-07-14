package admin.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        statelabel.setText(appointment.getState().toString());

        if (appointment.getState() == Appointment.State.BOOKED) {
            statelabel.setText(statelabel.getText() + " (" + appointment.getBooking().getGroup().toString() + ")");
        } else if (appointment.getState() == Appointment.State.RESERVED) {
            statelabel.setText(statelabel.getText() + " (" + appointment.getReservation().getGroup().toString() + ")");
        }
    }

    /**
     * Event Handler for editing the appointmennt
     *
     * @param event The click Event
     */
    public void edit(ActionEvent event) {
    }

    private static String getDateString(LocalDate date) {
        String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMAN);

        return day + " " + date.format(DateTimeFormatter.ofPattern("dd.MM.yy", new Locale("de")));
    }
}
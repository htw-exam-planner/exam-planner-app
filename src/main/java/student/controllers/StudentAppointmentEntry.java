package student.controllers;

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
import models.Booking;
import models.Group;
import shared.controller.AppointmentEntry;

import java.io.IOException;
import java.time.LocalTime;

public class StudentAppointmentEntry extends AppointmentEntry {

    @FXML
    Label daylabel;

    @FXML
    Label notelabel;

    @FXML
    Label statelabel;

    @FXML
    Button reservationbutton;

    @FXML
    Button bookbutton;

    private Group activeGroup;

    /**
     * Creates an StudentAppointmentEntry component
     *
     * @param activeGroup The currently selected Group
     * @param appointment The Appointment to display
     */
    public StudentAppointmentEntry(Group activeGroup, Appointment appointment) {
        super(appointment);

        this.activeGroup = activeGroup;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/student/views/StudentAppointmentEntry.fxml"));
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

        switch (appointment.getState()) {
            case BOOKED: {
                reservationbutton.setVisible(false);
                bookbutton.setVisible(false);
            }
            case DEACTIVATED: {
                reservationbutton.setVisible(false);
                bookbutton.setVisible(false);
                break;
            }
            case RESERVED: {
                if (appointment.getReservation().getGroup().equals(activeGroup)) {
                    reservationbutton.setText("Stornieren");
                    reservationbutton.setVisible(true);
                    bookbutton.setVisible(true);
                } else {
                    reservationbutton.setVisible(false);
                    bookbutton.setVisible(false);
                }
                break;
            }
            case FREE: {
                try {
                    if (activeGroup.hasReservation()) {
                        reservationbutton.setVisible(false);
                        bookbutton.setVisible(true);
                    } else if (activeGroup.hasBooking()) {
                        reservationbutton.setVisible(false);
                        bookbutton.setVisible(false);
                    } else {
                        reservationbutton.setText("Reservieren");
                        reservationbutton.setVisible(true);
                        bookbutton.setVisible(true);
                    }
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Gruppeninformationen konnten nicht geladen werden");
                    alert.showAndWait();
                    System.exit(1);
                }
                break;
            }
        }
    }

    /**
     * Event Handler for changing the reservation state (reserve or cancel)
     *
     * @param event The click Event
     */
    public void changeReservation(ActionEvent event) {
        try {
            if (appointment.getState() == Appointment.State.RESERVED) {
                appointment.cancelReservation();
                emitAppointmentUpdated();
            } else if (appointment.getState() == Appointment.State.FREE) {
                appointment.reserve(activeGroup);
                emitAppointmentUpdated();
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Termin konnte nicht aktualisiert werden");
            alert.showAndWait();
            System.exit(1);
        }
    }

    /**
     * Event Handler for booking the appointmennt
     *
     * @param event The click Event
     */
    public void book(ActionEvent event) {
        try {
            BookingDialog bookingDialog = new BookingDialog(appointment,activeGroup);
            bookingDialog.addEventHandler(BookingDialog.APPOINTMENT_UPDATED, e -> emitAppointmentUpdated());

            Stage dialog = new Stage();

            dialog.setScene(new Scene(bookingDialog));
            dialog.initOwner(((Node) event.getSource()).getScene().getWindow());
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.showAndWait();

        } catch (Exception e) {
            System.err.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Termin konnte nicht aktualisiert werden");
            alert.showAndWait();
            System.exit(1);
        }
    }
}

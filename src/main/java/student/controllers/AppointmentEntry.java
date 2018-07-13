package student.controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import models.Appointment;
import models.Group;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class AppointmentEntry extends HBox {
    public static final EventType<Event> APPOINTMENT_UPDATED =
            new EventType<>("APPOINTMENT_UPDATED");

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


    private Appointment appointment;
    private Group activeGroup;

    public AppointmentEntry(Group activeGroup, Appointment appointment) {
        this.activeGroup = activeGroup;
        this.appointment = appointment;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/student/views/AppointmentEntry.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public void paint() {
        daylabel.setText(getDateString(appointment.getDate()));
        notelabel.setText(appointment.getNote());
        statelabel.setText(appointment.getState().toString());

        switch (appointment.getState()) {
            case BOOKED: {
                reservationbutton.setVisible(false);
                bookbutton.setVisible(false);
                statelabel.setText(statelabel.getText() + " (" + appointment.getBooking().getGroup().toString() + ")");
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

                statelabel.setText(statelabel.getText() + " (" + appointment.getReservation().getGroup().toString() + ")");
                break;
            }
            case FREE: {
                try {
                    if (activeGroup.hasReservation() || activeGroup.hasBooking()) {
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

    public void changeReservation(ActionEvent event) throws Exception {
        if (appointment.getState() == Appointment.State.RESERVED) {
            appointment.cancelReservation();
            fireEvent(new Event(APPOINTMENT_UPDATED));
        } else if (appointment.getState() == Appointment.State.FREE) {
            appointment.reserve(activeGroup);
            fireEvent(new Event(APPOINTMENT_UPDATED));
        }
    }

    public void book(ActionEvent event) throws Exception {
        if (appointment.getState() == Appointment.State.FREE || appointment.getState() == Appointment.State.RESERVED) {
            appointment.book(activeGroup, LocalTime.of(9, 0));
            fireEvent(new Event(APPOINTMENT_UPDATED));
        }
    }

    public Appointment getAppointment() {
        return appointment;
    }

    private static String getDateString(LocalDate date) {
        String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMAN);

        return day + " " + date.format(DateTimeFormatter.ofPattern("dd.MM.yy", new Locale("de")));
    }
}

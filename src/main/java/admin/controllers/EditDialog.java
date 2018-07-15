package admin.controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Appointment;
import models.Booking;
import models.InvalidTimeWindowException;
import models.TimeWindow;

import java.io.IOException;
import java.time.LocalTime;

public class EditDialog extends AnchorPane {
    public static final EventType<Event> APPOINTMENT_UPDATED = new EventType<>("admin.controllers.EditDialog:APPOINTMENT_UPDATED");

    @FXML
    public TextField startHour;

    @FXML
    public TextField startMinute;

    @FXML
    public TextField endHour;

    @FXML
    public TextField endMinute;

    @FXML
    public TextField note;

    @FXML
    public AnchorPane editBookingContainer;

    @FXML
    public TextField bookingStartHour;

    @FXML
    public TextField bookingStartMinute;

    @FXML
    public TextField bookingEndHour;

    @FXML
    public TextField bookingEndMinute;

    @FXML
    public TextField bookingRoom;

    @FXML
    public Button changeActivationButton;

    @FXML
    public Button setFreeButton;

    private Appointment appointment;

    public EditDialog(Appointment appointment) {
        this.appointment = appointment;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/admin/views/EditDialog.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Sets up the Edit View by filling the fields with the current values
     */
    @FXML
    public void initialize() {
        final TimeWindow timeWindow = appointment.getTimeWindow();

        startHour.setText(formatTimeValue(timeWindow.getStart().getHour()));
        startMinute.setText(formatTimeValue(timeWindow.getStart().getMinute()));
        endHour.setText(formatTimeValue(timeWindow.getEnd().getHour()));
        endMinute.setText(formatTimeValue(timeWindow.getEnd().getMinute()));
        note.setText(appointment.getNote() == null ? "" : appointment.getNote());

        switch (appointment.getState()) {
            case BOOKED: {
                final Booking booking = appointment.getBooking();
                editBookingContainer.setVisible(true);
                bookingStartHour.setText(formatTimeValue(booking.getTimeWindow().getStart().getHour()));
                bookingStartMinute.setText(formatTimeValue(booking.getTimeWindow().getStart().getMinute()));
                bookingRoom.setText(booking.getRoom() == null ? "" : booking.getRoom());

                if (booking.getTimeWindow().getEnd() != null) {
                    bookingEndHour.setText(formatTimeValue(booking.getTimeWindow().getEnd().getHour()));
                    bookingEndMinute.setText(formatTimeValue(booking.getTimeWindow().getEnd().getMinute()));
                } else {
                    bookingEndHour.setText("");
                    bookingEndMinute.setText("");
                }

                setFreeButton.setVisible(true);

                changeActivationButton.setText("Deaktivieren");
                break;
            }
            case RESERVED: {
                editBookingContainer.setVisible(false);
                setFreeButton.setVisible(true);

                changeActivationButton.setText("Deaktivieren");
                break;
            }
            case DEACTIVATED: {
                editBookingContainer.setVisible(false);
                setFreeButton.setVisible(false);

                changeActivationButton.setText("Aktivieren");
                break;
            }
            case FREE: {
                editBookingContainer.setVisible(false);
                setFreeButton.setVisible(false);

                changeActivationButton.setText("Deaktivieren");
                break;
            }
        }
    }

    /**
     * Saves the edits on the Appointment
     *
     * @param event the event causing the method to be called
     */
    public void save(ActionEvent event) {
        try {
            final TimeWindow newTimeWindow = new TimeWindow(LocalTime.of(
                    Integer.parseInt(patchTimeValue(startHour.getText())),
                    Integer.parseInt(patchTimeValue(startMinute.getText()))
            ), LocalTime.of(
                    Integer.parseInt(patchTimeValue(endHour.getText())),
                    Integer.parseInt(patchTimeValue(endMinute.getText()))
            ));

            if (!appointment.getTimeWindow().equals(newTimeWindow)) {
                appointment.setTimeWindow(newTimeWindow);
            }

            if (!(appointment.getNote() == null ? "" : appointment.getNote()).equals(note.getText())) {
                appointment.setNote(note.getText());
            }


            if (appointment.getState() == Appointment.State.BOOKED) {
                final Booking booking = appointment.getBooking();
                TimeWindow newBookingTimeWindow;

                if (bookingEndHour.getText().equals("") && bookingEndMinute.getText().equals("")) {
                    newBookingTimeWindow = new TimeWindow(LocalTime.of(
                            Integer.parseInt(patchTimeValue(bookingStartHour.getText())),
                            Integer.parseInt(patchTimeValue(bookingStartMinute.getText()))
                    ));
                } else {
                    newBookingTimeWindow = new TimeWindow(LocalTime.of(
                            Integer.parseInt(patchTimeValue(bookingStartHour.getText())),
                            Integer.parseInt(patchTimeValue(bookingStartMinute.getText()))
                    ), LocalTime.of(
                            Integer.parseInt(patchTimeValue(bookingEndHour.getText())),
                            Integer.parseInt(patchTimeValue(bookingEndMinute.getText()))
                    ));
                }


                if (!booking.getTimeWindow().equals(newBookingTimeWindow)) {
                    booking.updateTimeWindow(newBookingTimeWindow);
                }

                if (!(booking.getRoom() == null ? "" : booking.getRoom()).equals(bookingRoom.getText())) {
                    booking.updateRoom(bookingRoom.getText());
                }
            }

            fireEvent(new Event(APPOINTMENT_UPDATED));

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (InvalidTimeWindowException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Startzeit muss vor Endzeit liegen");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Termin konnte nicht aktualisiert werden");
            alert.showAndWait();
            System.exit(1);
        }
    }

    /**
     * Closes the editor without saving
     *
     * @param event the event causing the method to be called
     */
    public void close(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }

    /**
     * Toggle the activation state of the appointment
     *
     * @param event the event causing the method to be called
     */
    public void toggleActivation(ActionEvent event) {
        try {
            if (appointment.getState() == Appointment.State.DEACTIVATED) {
                appointment.setFree();
            } else {
                appointment.deactivate();
            }

            fireEvent(new Event(APPOINTMENT_UPDATED));

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Termin konnte nicht aktualisiert werden");
            alert.showAndWait();
            System.exit(1);
        }
    }

    /**
     * Cancels an existing Booking or Reservation
     *
     * @param event the event causing the method to be called
     */
    public void setFree(ActionEvent event) {
        try {
            if (appointment.getState() == Appointment.State.BOOKED || appointment.getState() == Appointment.State.RESERVED) {
                appointment.setFree();
            }
            fireEvent(new Event(APPOINTMENT_UPDATED));

            initialize();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Termin konnte nicht aktualisiert werden");
            alert.showAndWait();
            System.exit(1);
        }
    }

    private static String formatTimeValue(int timeValue) {
        return String.format("%02d", timeValue);
    }

    private static String patchTimeValue(String val) {
        return val.equals("") ? "0" : val;
    }
}

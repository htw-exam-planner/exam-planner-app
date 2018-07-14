package admin.controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Appointment;
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
     *
     */
    @FXML
    public void initialize() {
        final TimeWindow timeWindow =  appointment.getTimeWindow();

        startHour.setText(String.format("%02d", timeWindow.getStart().getHour()));
        startMinute.setText(String.format("%02d", timeWindow.getStart().getMinute()));
        endHour.setText(String.format("%02d", timeWindow.getEnd().getHour()));
        endMinute.setText(String.format("%02d", timeWindow.getEnd().getMinute()));
        note.setText(appointment.getNote() == null ? "" : appointment.getNote());
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

    private static String patchTimeValue(String val) {
        return val.equals("") ? "0" : val;
    }
}

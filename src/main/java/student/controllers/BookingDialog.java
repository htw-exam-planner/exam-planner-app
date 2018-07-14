package student.controllers;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.*;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class BookingDialog extends AnchorPane {
    public static final EventType<Event> APPOINTMENT_UPDATED =
            new EventType<>("student.controllers.BookingDialog:APPOINTMENT_UPDATED");

    private Appointment appointment;
    private Group activeGroup;

    @FXML
    Label appointmentLabel;

    @FXML
    Label startTimeLabel;

    @FXML
    TextField hourField;

    @FXML
    TextField minuteField;

    /**
     * Constructs a new BookingDialog
     * @param appointment The Appointment to be booked
     * @param activeGroup The Group wishing to book
     */
    public BookingDialog(Appointment appointment, Group activeGroup) {
        this.appointment = appointment;
        this.activeGroup = activeGroup;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/student/views/BookingDialog.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Buchungsansicht konnte nicht geladen werden");
            alert.showAndWait();
            System.exit(1);
        }
    }

    /**
     * Initializes the labels with the data of the Appointment
     */
    @FXML
    public void initialize(){
        appointmentLabel.setText("Termin buchen ("+
                appointment.getDate().format(DateTimeFormatter.ofPattern("dd.MM.yy",new Locale("de"))) +
                ")"
        );

        startTimeLabel.setText("Startzeit (" + appointment.getTimeWindow().toString() + ")");
    }

    /**
     * Books the Appointment
     * @param event The click event
     */
    public void book(ActionEvent event){
        try {
            if(hourField.getText().equals("") || minuteField.getText().equals("") ){
                throw new IllegalArgumentException();
            }

            LocalTime starTime = LocalTime.of(Integer.parseInt(hourField.getText()),
                    Integer.parseInt(minuteField.getText()));

            appointment.book(activeGroup,starTime);

            fireEvent(new Event(APPOINTMENT_UPDATED));

            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
        }
        catch (IllegalArgumentException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR,"Bitte geben Sie eine gültige Startzeit ein");
        }
        catch (OperationNotAllowedException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Die Startzeit muss im Zeitfenster liegen");
            alert.showAndWait();
        }
        catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Fehler beim Erstellen der Buchung");
            alert.showAndWait();
            System.exit(1);
        }
    }

    /**
     * Closes the dialog without any action
     * @param event The click event
     */
    public void cancel(ActionEvent event){
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}

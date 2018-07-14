package shared.controller;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import models.Appointment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public abstract class AppointmentEntry extends HBox {
    public static final EventType<Event> APPOINTMENT_UPDATED =
            new EventType<>("shared.controller.AppointmentEntry:APPOINTMENT_UPDATED");

    protected Appointment appointment;

    /**
     * Creates an AppointmentEntry component
     *
     * @param appointment The Appointment to display
     */
    public AppointmentEntry(Appointment appointment) {
        this.appointment = appointment;
    }

    /**
     * Displays/Updates the Appointment information in the component
     */
    public abstract void paint();

    /**
     * Returns the displayed Appointment
     *
     * @return The displayed Appointment
     */
    public Appointment getAppointment() {
        return appointment;
    }

    protected void emitAppointmentUpdated() {
        fireEvent(new Event(APPOINTMENT_UPDATED));
    }

    protected static String getDateString(LocalDate date) {
        String day = date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.GERMAN);

        return day + " " + date.format(DateTimeFormatter.ofPattern("dd.MM.yy", new Locale("de")));
    }

    /**
     * Returns the Color for the State label
     *
     * @return GREEN for free appointments, gray for deactivated, goldenrod for reserved and red for booked
     */
    protected Color getStateColor(){
        switch (appointment.getState()){
            case FREE:
                return Color.GREEN;
            case DEACTIVATED:
                return Color.GRAY;
            case RESERVED:
                return Color.GOLDENROD;
            case BOOKED:
                return Color.RED;
        }
        return Color.BLACK; //Never reached, but else return statement is missing
    }

    /**
     * Returns the String for the state label
     *
     * @return a String representation of the state, with information about reservations or bookings
     */
    protected String getStateString(){
        String stateString = appointment.getState().toString();

        switch (appointment.getState()){
            case BOOKED:
                return stateString + " (" + appointment.getBooking().toString() + ")";
            case RESERVED:
                return stateString + " (" + appointment.getReservation().getGroup().toString() + ")";
            case DEACTIVATED:
                return stateString;
            case FREE:
                return stateString + " " + appointment.getTimeWindow().toString();
        }
        return stateString; //Never reached, but else return statement is missing
    }
}

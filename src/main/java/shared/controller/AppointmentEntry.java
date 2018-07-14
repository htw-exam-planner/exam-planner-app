package shared.controller;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.layout.HBox;
import models.Appointment;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public abstract class AppointmentEntry extends HBox {
    public static final EventType<Event> APPOINTMENT_UPDATED =
            new EventType<>("APPOINTMENT_UPDATED");

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
}

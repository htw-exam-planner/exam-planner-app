package shared.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.Appointment;

import java.io.IOException;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class Appointments extends AnchorPane {
    @FXML
    VBox entries;

    /**
     * Loads the JavaFX view component
     *
     * This can not be done in the constructor, because initialization logic of extended classes might not be done otherwise
     */
    protected void initializeView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/shared/views/AppointmentView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Puts the group names into the groupsChoice choice box
     */
    @FXML
    public void initialize() {
        showAppointments();
    }

    /**
     * Adds the appointments to the view
     */
    private void showAppointments() {
        try {
            entries.getChildren().clear();
            Map<Integer, List<AppointmentEntry>> groupedAppointmentEntries = Appointment.all().stream()
                    .map(a -> {
                        AppointmentEntry entry = createAppointmentEntry(a);
                        entry.addEventHandler(AppointmentEntry.APPOINTMENT_UPDATED, event -> showAppointments());
                        entry.paint();
                        return entry;
                    })
                    .collect(Collectors.groupingBy(e ->
                            e.getAppointment().getDate().get(WeekFields.of(Locale.GERMAN).weekOfWeekBasedYear())));

            groupedAppointmentEntries.forEach((week, appointmentEntries) -> {
                entries.getChildren().add(new Label("Woche " + week.toString()));
                entries.getChildren().addAll(appointmentEntries);
            });
        } catch (Exception e) {
            System.err.println(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Termine konnten nicht geladen werden");
            alert.showAndWait();
            System.exit(1);
        }
    }

    protected abstract AppointmentEntry createAppointmentEntry(Appointment appointment);
}

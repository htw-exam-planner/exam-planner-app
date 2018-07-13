package student.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.Appointment;
import models.Group;

import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentAppointmentController {
    @FXML
    VBox entries;

    private Group group;

    /**
     * Create the controller for the StudentAppointmentView
     *
     * @param group The currently active group
     */
    public StudentAppointmentController(Group group) {
        this.group = group;
    }

    /**
     * Puts the group names into the groupsChoice choice box
     */
    @FXML
    public void initialize() {
        showAppointments();
    }

    private void showAppointments() {
        try {
            entries.getChildren().clear();
            Map<Integer, List<AppointmentEntry>> groupedAppointmentEntries = Appointment.all().stream()
                    .map(a -> {
                        AppointmentEntry entry = new AppointmentEntry(group, a);
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
}

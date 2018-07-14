package student.controllers;

import models.Appointment;
import models.Group;
import shared.controller.Appointments;
import shared.controller.AppointmentEntry;

public class StudentAppointments extends Appointments {
    private Group group;

    /**
     * Create the controller for the StudentAppointmentView
     *
     * @param group The currently active group
     */
    public StudentAppointments(Group group) {
        this.group = group;
        initializeView();
    }

    @Override
    protected AppointmentEntry createAppointmentEntry(Appointment appointment) {
        return new StudentAppointmentEntry(group, appointment);
    }
}

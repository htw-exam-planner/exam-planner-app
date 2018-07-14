package admin.controllers;

import models.Appointment;
import shared.controller.Appointments;
import shared.controller.AppointmentEntry;

public class AdminAppointments extends Appointments {
    public AdminAppointments() {
        initializeView();
    }

    @Override
    protected AppointmentEntry createAppointmentEntry(Appointment appointment) {
        return new AdminAppointmentEntry(appointment);
    }
}

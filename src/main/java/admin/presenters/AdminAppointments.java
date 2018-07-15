package admin.presenters;

import models.Appointment;
import shared.presenters.Appointments;
import shared.presenters.AppointmentEntry;

public class AdminAppointments extends Appointments {
    public AdminAppointments() {
        initializeView();
    }

    @Override
    protected AppointmentEntry createAppointmentEntry(Appointment appointment) {
        return new AdminAppointmentEntry(appointment);
    }
}

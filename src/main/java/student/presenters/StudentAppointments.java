package student.presenters;

import models.Appointment;
import models.Group;
import shared.presenters.Appointments;
import shared.presenters.AppointmentEntry;

public class StudentAppointments extends Appointments {
    private Group group;

    /**
     * Create the presenters for the StudentAppointmentView
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

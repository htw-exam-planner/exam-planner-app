package models;

import repository.DBRepository;
import repository.RepositoryConnectionException;

import java.sql.SQLException;
import java.util.*;

public class Group {
    private int groupNumber;

    /**
     * Constructs a new group
     * @param groupNumber Number of the group
     */
    public Group(int groupNumber){
        this.groupNumber = groupNumber;
    }

    public int getGroupNumber() {
        return groupNumber;
    }

    /**
     * Gets all groups
     * @return a list of all groups
     * @throws Exception if an exception is thrown while getting the groups
     */
    public static List<Group> all() throws Exception {
        DBRepository repository;
        List<Group> groups;
        try {
            repository = DBRepository.getInstance();

            groups = repository.getGroups();
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Exception while getting groups from database");
        }

        return groups;
    }

    /**
     * Determines if the other object is a Group with the same number
     * @param obj the object for comparison
     * @return true if and only if obj is a Group with a group number equal to this, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Group){
            Group other = (Group) obj;
            return this.groupNumber == other.groupNumber;
        }
        else return false;
    }

    /**
     * Determines if a group has a reservation
     * @return true if there is an Appointment with state RESERVED reserved by this group, false otherwise
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public boolean hasReservation() throws SQLException, RepositoryConnectionException, InvalidAppointmentStateException {
        return Appointment.all().stream()
                .filter(appointment -> appointment.getState() == Appointment.State.RESERVED)
                .filter(appointment -> appointment.getReservation().getGroup().equals(this)).count() != 0;
    }

    /**
     * Determines if a group has a reservation
     * @return true if there is an Appointment with state BOOKED booked by this group, false otherwise
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public boolean hasBooking() throws SQLException, RepositoryConnectionException, InvalidAppointmentStateException {
        return Appointment.all().stream()
                .filter(appointment -> appointment.getState() == Appointment.State.BOOKED)
                .filter(appointment -> appointment.getBooking().getGroup().equals(this)).count() != 0;
    }

    /**
     * Gets the Appointment booked or reserved by a group
     * @return the Appointment booked or reserved by the group as an Optional, or an empty Optional if
     * the group has not booked or reserved any Appointment
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public Optional<Appointment> getAppointment() throws SQLException, RepositoryConnectionException, InvalidAppointmentStateException {
        Optional<Appointment> bookedAppointment =  Appointment.all().stream()
                .filter(appointment ->
                        (appointment.getState() == Appointment.State.BOOKED))
                .filter(appointment -> appointment.getBooking().getGroup().equals(this))
                .findFirst();

        Optional<Appointment> reservedAppointment =  Appointment.all().stream()
                .filter(appointment ->
                        (appointment.getState() == Appointment.State.RESERVED))
                .filter(appointment -> appointment.getReservation().getGroup().equals(this))
                .findFirst();

        return bookedAppointment.isPresent() ? bookedAppointment : reservedAppointment;
    }

    /**
     * convert a Group to String
     * @return Group name as "Gruppe &lt;number&gt;"
     */
    @Override
    public String toString() {
        return "Gruppe "+groupNumber;
    }
}

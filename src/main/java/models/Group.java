package models;

import repository.DBRepository;
import repository.RepositoryConnectionException;

import java.sql.SQLException;
import java.util.*;

public class Group {
    private int number;

    /**
     * Constructs a new group
     * @param number Number of the group
     */
    public Group(int number){
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    /**
     * Gets all groups
     * @return a list of all groups
     * @throws RepositoryConnectionException if the connection to the repository fails
     * @throws SQLException if an SQL error occurs
     */
    public static List<Group> all() throws RepositoryConnectionException, SQLException {
        return DBRepository.getInstance().getGroups();
    }

    public static void generate(int numberOfGroups) throws RepositoryConnectionException, SQLException {
        if(numberOfGroups < 1)
            throw new IllegalArgumentException();

        DBRepository repository = DBRepository.getInstance();

        repository.deleteAllGroups();

        for(int i = 1 ; i <= numberOfGroups ; i++){
            Group group = new Group(i);
            repository.insertGroup(group);
        }
    }

    public static void delete(Group group) throws RepositoryConnectionException, SQLException {
        DBRepository.getInstance().deleteGroup(group);
    }

    /**
     * Creates a new group with the next available number and stores it in the database
     * @return the created group
     * @throws RepositoryConnectionException if the connection to the repository fails
     * @throws SQLException if an SQL error occurs
     */
    public static Group create() throws RepositoryConnectionException, SQLException {
        Optional<Group> maxGroup = all().stream()
                .sorted(Comparator.comparing(Group::getNumber).reversed())
                .findFirst();

        int groupNo = maxGroup.map(g -> g.number + 1).orElse(1);

        Group group = new Group(groupNo);

        DBRepository.getInstance().insertGroup(group);

        return group;
    }

    /**
     * Determines if the other object is a Group with the same number
     * @param obj the object for comparison
     * @return true if and only if obj is a Group with a group number equal to this, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Group) && (this.number == ((Group) obj).number);
    }

    /**
     * Determines if a group has a reservation
     * @return true if there is an Appointment with state RESERVED reserved by this group, false otherwise
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws InvalidTimeWindowException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public boolean hasReservation() throws SQLException, RepositoryConnectionException, InvalidAppointmentStateException, InvalidTimeWindowException {
        return Appointment.all().stream()
                .filter(appointment -> appointment.getState() == Appointment.State.RESERVED)
                .filter(appointment -> appointment.getReservation().getGroup().equals(this)).count() != 0;
    }

    /**
     * Determines if a group has a reservation
     * @return true if there is an Appointment with state BOOKED booked by this group, false otherwise
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws InvalidTimeWindowException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public boolean hasBooking() throws SQLException, RepositoryConnectionException, InvalidAppointmentStateException, InvalidTimeWindowException {
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
     * @throws InvalidTimeWindowException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public Optional<Appointment> getAppointment() throws SQLException, RepositoryConnectionException, InvalidAppointmentStateException, InvalidTimeWindowException {
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
        return "Gruppe "+ number;
    }
}

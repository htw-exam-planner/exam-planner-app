package models;

import repository.DBRepository;
import repository.RepositoryConnectionException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

public class Appointment {
    public enum State {FREE,DEACTIVATED,RESERVED,BOOKED}

    private LocalDate date;
    private LocalTime timeWindowStart;
    private LocalTime timeWindowEnd;
    private String note;
    private State state;
    private Group group;
    private LocalTime bookingStart;
    private LocalTime bookingEnd;
    private String room;

    /**
     * Constructs a new appointment booked by a group
     * @param date The appointment's date
     * @param timeWindowStart The start of the appointment's time window
     * @param timeWindowEnd The end of the appointment's time window
     * @param note The appointment's note
     * @param state The appointment's state
     * @param group The group that booked the appointment
     * @param bookingStart The start time of the booking
     * @param bookingEnd The end time of the booking
     * @param room The room of the booking
     * @throws InvalidAppointmentStateException if the state is not State.BOOKED
     */
    public Appointment(LocalDate date, LocalTime timeWindowStart, LocalTime timeWindowEnd, String note,
                       State state, Group group, LocalTime bookingStart, LocalTime bookingEnd, String room)
            throws InvalidAppointmentStateException {

        switch (state){
            case DEACTIVATED:
            case FREE:
                if(!(group==null && bookingStart==null && bookingEnd == null && room ==null)){
                    throw new InvalidAppointmentStateException();
                }
                break;
            case RESERVED:
                if(!(group!=null && bookingStart==null && bookingEnd == null && room ==null)){
                    throw new InvalidAppointmentStateException();
                }
                break;
            case BOOKED:
                if(!(group!=null && bookingStart != null)){
                    throw new InvalidAppointmentStateException();
                }
                break;
        }

        this.date = date;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
        this.note = note;
        this.state = state;
        this.group = group;
        this.bookingStart = bookingStart;
        this.bookingEnd = bookingEnd;
        this.room = room;
    }

    /**
     * Gets the date of the appointment
     * @return the appointment's date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the start of the time window
     * @return the start of the time window
     */
    public LocalTime getTimeWindowStart() {
        return timeWindowStart;
    }

    /**
     * Gets the end of the time window
     * @return the end of the time window
     */
    public LocalTime getTimeWindowEnd() {
        return timeWindowEnd;
    }

    /**
     * Gets the appointment's note
     * @return the appointment's note
     */
    public String getNote() {
        return note;
    }

    /**
     * Gets the appointment's state
     * @return the appointment's state
     */
    public State getState() {
        return state;
    }

    /**
     * Gets the group that booked or reserved the appointment
     * @return the group that booked or reserved the appointment or null if the appointment is free or deactivated
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Gets the start of the booked time
     * @return the start of the booked time or null if the appointment is not booked
     */
    public LocalTime getBookingStart() {
        return bookingStart;
    }

    /**
     * Gets the end of the booked time
     * @return the end of the booked time or null if the appointment is not booked or the end time is not set
     */
    public LocalTime getBookingEnd() {
        return bookingEnd;
    }

    /**
     * Gets the room of the booked appointment
     * @return the room or null if the room is not set or the appointment is not booked
     */
    public String getRoom() {
        return room;
    }

    /**
     * Reserves an Appointment
     * @param reservingGroup the group wishing to reserve the appointment
     * @throws OperationNotAllowedException if the Appointment is not free or the group
     * has reserved or booked a different Appointment
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public void reserve(Group reservingGroup) throws OperationNotAllowedException, SQLException,
            RepositoryConnectionException, InvalidAppointmentStateException {
        if(state != State.FREE) throw new OperationNotAllowedException();

        if(reservingGroup.hasBooking() || reservingGroup.hasReservation()){
            throw new OperationNotAllowedException();
        }

        state = State.RESERVED;
        group=reservingGroup;

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Cancels the reservation of an appointment
     * @throws OperationNotAllowedException if the appointment is not reserved
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws SQLException if an SQL error occurs
     */
    public void cancelReservation() throws OperationNotAllowedException, RepositoryConnectionException, SQLException {
        if(state!=State.RESERVED) throw new OperationNotAllowedException();

        state = State.FREE;

        group = null;

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Books an appointment
     * @param bookingGroup the group wishing book the appointment
     * @param bookingStart the start time of the booked time window
     * @throws OperationNotAllowedException if the appointment is already booked or reserved by a different group or
     * the booking group has already booked or reserved
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public void book(Group bookingGroup, LocalTime bookingStart) throws OperationNotAllowedException, SQLException,
            RepositoryConnectionException, InvalidAppointmentStateException {
        if(state == State.DEACTIVATED || state == State.BOOKED) throw new OperationNotAllowedException();

        if(state == State.RESERVED && !this.group.equals(bookingGroup)) throw new OperationNotAllowedException();

        if(bookingGroup.hasBooking()) throw new OperationNotAllowedException();

        if(bookingGroup.hasReservation()) {
            Appointment reservedAppointment = bookingGroup.getAppointment().get();

            reservedAppointment.cancelReservation();
        }

        this.state = State.BOOKED;
        this.group = bookingGroup;
        this.bookingStart = bookingStart;

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Gets all appointments from the database
     * @return A list of all appointments
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws SQLException if an SQL error occurs
     */
    public static List<Appointment> all() throws RepositoryConnectionException,
            InvalidAppointmentStateException, SQLException {
        DBRepository repository = DBRepository.getInstance();

        return repository.getAppointments();
    }
}

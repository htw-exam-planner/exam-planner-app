package models;

import repository.DBRepository;
import repository.RepositoryConnectionException;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Appointment {
    public enum State {FREE,DEACTIVATED,RESERVED,BOOKED}

    private LocalDate date;
    private LocalTime timeWindowStart;
    private LocalTime timeWindowEnd;
    private String note;
    private State state;
    private Reservation reservation=null;
    private Booking booking=null;

    /**
     * General constructor used by other constructors for fields present in every state
     * @param date The appointment's date
     * @param timeWindowStart The start of the appointment's time window
     * @param timeWindowEnd The end of the appointment's time window
     * @param note The appointment's note
     */
    private Appointment(LocalDate date, LocalTime timeWindowStart, LocalTime timeWindowEnd, String note) {
        this.date = date;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
        this.note = note;
    }

    /**
     * Constructor for free or deactivated appointments
     * @param date The appointment's date
     * @param timeWindowStart The start of the appointment's time window
     * @param timeWindowEnd The end of the appointment's time window
     * @param note The appointment's note
     * @param state The appointment's state
     * @throws InvalidAppointmentStateException if the state is not State.FREE or State.DEACTIVATED
     */
    public Appointment(LocalDate date, LocalTime timeWindowStart, LocalTime timeWindowEnd, String note, State state)
            throws InvalidAppointmentStateException {
        this(date,timeWindowStart,timeWindowEnd,note);

        if(!(state==State.FREE || state == State.DEACTIVATED)) throw new InvalidAppointmentStateException();
        this.state = state;
    }

    /**
     * Constructor for reserved appointments
     * @param date The appointment's date
     * @param timeWindowStart The start of the appointment's time window
     * @param timeWindowEnd The end of the appointment's time window
     * @param note The appointment's note
     * @param state The appointment's state
     * @param reservation The appointment's reservation
     * @throws InvalidAppointmentStateException if the state is not State.RESERVED
     */
    public Appointment(LocalDate date, LocalTime timeWindowStart, LocalTime timeWindowEnd, String note, State state, Reservation reservation) throws InvalidAppointmentStateException {
        this(date,timeWindowStart,timeWindowEnd,note);

        if(state!=State.RESERVED) throw new InvalidAppointmentStateException();
        this.state = state;
        this.reservation = reservation;
    }

    /**
     * Constructor for booked appointments
     * @param date The appointment's date
     * @param timeWindowStart The start of the appointment's time window
     * @param timeWindowEnd The end of the appointment's time window
     * @param note The appointment's note
     * @param state The appointment's state
     * @param booking The appointment's booking
     * @throws InvalidAppointmentStateException if the state is not State.BOOKED
     */
    public Appointment(LocalDate date, LocalTime timeWindowStart, LocalTime timeWindowEnd, String note, State state, Booking booking) throws InvalidAppointmentStateException {
        this(date,timeWindowStart,timeWindowEnd,note);

        if(state!=State.BOOKED) throw new InvalidAppointmentStateException();
        this.state = state;
        this.booking = booking;
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
     * Gets the appointment's reservation
     * @return the reservation if the appointment is reserved, null otherwise
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Gets the appointment's booking
     * @return the booking if the appointment is booked, null otherwise
     */
    public Booking getBooking() {
        return booking;
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
        reservation = new Reservation(reservingGroup);

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

        reservation = null;

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

        if(state == State.RESERVED && !this.reservation.getGroup().equals(bookingGroup))
            throw new OperationNotAllowedException();

        if(bookingGroup.hasBooking()) throw new OperationNotAllowedException();

        if(bookingGroup.hasReservation()) {
            Appointment reservedAppointment = bookingGroup.getAppointment().get();
            reservedAppointment.cancelReservation();
        }

        this.state = State.BOOKED;
        this.booking = new Booking(bookingGroup,bookingStart,null,null);

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

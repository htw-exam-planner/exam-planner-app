package models;

import repository.DBRepository;
import repository.RepositoryConnectionException;

import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Appointment {
    public enum State {
        FREE("Frei"), DEACTIVATED("Deaktiviert"), RESERVED("Reserviert"), BOOKED("Gebucht");
        private String stringRepresenntation;

        State(String stringRepresenntation) {
            this.stringRepresenntation = stringRepresenntation;
        }

        @Override
        public String toString() {
            return stringRepresenntation;
        }
    }

    private LocalDate date;
    private TimeWindow timeWindow;
    private String note;
    private State state;
    private Reservation reservation = null;
    private Booking booking = null;

    /**
     * General constructor used by other constructors for fields present in every state
     *
     * @param date       The appointment's date
     * @param timeWindow the appointment's time window
     * @param note       The appointment's note
     */
    private Appointment(LocalDate date, TimeWindow timeWindow, String note) {
        this.date = date;
        this.timeWindow = timeWindow;
        this.note = note;
    }

    /**
     * Constructor for free or deactivated appointments
     *
     * @param date       The appointment's date
     * @param timeWindow The appointment's time window
     * @param note       The appointment's note
     * @param state      The appointment's state
     * @throws InvalidAppointmentStateException if the state is not State.FREE or State.DEACTIVATED
     */
    public Appointment(LocalDate date, TimeWindow timeWindow, String note, State state)
            throws InvalidAppointmentStateException {
        this(date, timeWindow, note);

        if (!(state == State.FREE || state == State.DEACTIVATED)) throw new InvalidAppointmentStateException();
        this.state = state;
    }

    /**
     * Constructor for reserved appointments
     *
     * @param date        The appointment's date
     * @param timeWindow  the appointment's time window
     * @param note        The appointment's note
     * @param state       The appointment's state
     * @param reservation The appointment's reservation
     * @throws InvalidAppointmentStateException if the state is not State.RESERVED
     */
    public Appointment(LocalDate date, TimeWindow timeWindow, String note, State state, Reservation reservation) throws InvalidAppointmentStateException {
        this(date, timeWindow, note);

        if (state != State.RESERVED) throw new InvalidAppointmentStateException();
        this.state = state;
        this.reservation = reservation;
    }

    /**
     * Constructor for booked appointments
     *
     * @param date       The appointment's date
     * @param timeWindow the appointment's time window
     * @param note       The appointment's note
     * @param state      The appointment's state
     * @param booking    The appointment's booking
     * @throws InvalidAppointmentStateException if the state is not State.BOOKED
     */
    public Appointment(LocalDate date, TimeWindow timeWindow, String note, State state, Booking booking) throws InvalidAppointmentStateException {
        this(date, timeWindow, note);

        if (state != State.BOOKED) throw new InvalidAppointmentStateException();
        this.state = state;
        this.booking = booking;
    }

    /**
     * Gets the date of the appointment
     *
     * @return the appointment's date
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Gets the appointment's time window
     *
     * @return
     */
    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    /**
     * Gets the appointment's note
     *
     * @return the appointment's note
     */
    public String getNote() {
        return note;
    }

    /**
     * Gets the appointment's state
     *
     * @return the appointment's state
     */
    public State getState() {
        return state;
    }

    /**
     * Gets the appointment's reservation
     *
     * @return the reservation if the appointment is reserved, null otherwise
     */
    public Reservation getReservation() {
        return reservation;
    }

    /**
     * Gets the appointment's booking
     *
     * @return the booking if the appointment is booked, null otherwise
     */
    public Booking getBooking() {
        return booking;
    }

    /**
     * Reserves an Appointment
     *
     * @param reservingGroup the group wishing to reserve the appointment
     * @throws OperationNotAllowedException     if the Appointment is not free or the group
     *                                          has reserved or booked a different Appointment
     * @throws RepositoryConnectionException    if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws SQLException                     if an SQL error occurs
     */
    public void reserve(Group reservingGroup) throws OperationNotAllowedException, SQLException,
            RepositoryConnectionException, InvalidAppointmentStateException, InvalidTimeWindowException {
        if (state != State.FREE) throw new OperationNotAllowedException();

        if (reservingGroup.hasBooking() || reservingGroup.hasReservation()) {
            throw new OperationNotAllowedException();
        }

        state = State.RESERVED;
        reservation = new Reservation(reservingGroup);

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Cancels the reservation of an appointment
     *
     * @throws OperationNotAllowedException  if the appointment is not reserved
     * @throws RepositoryConnectionException if the connection to the repository failed
     * @throws SQLException                  if an SQL error occurs
     */
    public void cancelReservation() throws OperationNotAllowedException, RepositoryConnectionException, SQLException {
        if (state != State.RESERVED) throw new OperationNotAllowedException();

        state = State.FREE;

        reservation = null;

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Books an appointment
     *
     * @param bookingGroup the group wishing book the appointment
     * @param bookingStart the start time of the booked time window
     * @throws OperationNotAllowedException     if the appointment is already booked or reserved by a different group or
     *                                          the booking group has already booked or reserved, or the start time is not within the time window
     * @throws RepositoryConnectionException    if the connection to the repository fails
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws InvalidTimeWindowException       if an appointment in the database has an invalid time window
     * @throws SQLException                     if an SQL error occurs
     */
    public void book(Group bookingGroup, LocalTime bookingStart) throws OperationNotAllowedException, SQLException,
            RepositoryConnectionException, InvalidAppointmentStateException, InvalidTimeWindowException {
        if (state == State.DEACTIVATED || state == State.BOOKED) throw new OperationNotAllowedException();

        if (state == State.RESERVED && !this.reservation.getGroup().equals(bookingGroup))
            throw new OperationNotAllowedException();

        if (bookingGroup.hasBooking()) throw new OperationNotAllowedException();

        if (!timeWindow.contains(bookingStart))
            throw new OperationNotAllowedException();

        if (bookingGroup.hasReservation()) {
            Appointment reservedAppointment = bookingGroup.getAppointment().get();
            reservedAppointment.cancelReservation();
        }

        this.state = State.BOOKED;
        this.booking = new Booking(bookingGroup, new TimeWindow(bookingStart), null);

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Sets the appointment's state to State.FREE, removing a booking or reservation if present
     *
     * @throws RepositoryConnectionException if the connection to the repository fails
     * @throws SQLException                  if an SQL error occurs
     */
    public void setFree() throws RepositoryConnectionException, SQLException {
        reservation = null;
        booking = null;
        state = State.FREE;

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Sets the appointment's state to State.DEACTIVATED, removing a booking or reservation if present
     *
     * @throws RepositoryConnectionException if the connection to the repository fails
     * @throws SQLException                  if an SQL error occurs
     */
    public void deactivate() throws RepositoryConnectionException, SQLException {
        reservation = null;
        booking = null;
        state = State.DEACTIVATED;

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Sets the appointment's time window
     *
     * @param timeWindow the new time window
     * @throws RepositoryConnectionException if the connection to the repository fails
     * @throws SQLException                  if an SQL error occurs
     */
    public void setTimeWindow(TimeWindow timeWindow) throws RepositoryConnectionException, SQLException {
        this.timeWindow = timeWindow;

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Sets the Appointment's note
     *
     * @param note the note
     * @throws RepositoryConnectionException if the connection to the repository fails
     * @throws SQLException                  if an SQL error occurs
     */
    public void setNote(String note) throws RepositoryConnectionException, SQLException {
        this.note = note;

        DBRepository.getInstance().updateAppointment(this);
    }

    /**
     * Generates 15 free Appointments on the 15 working days starting on startDate, with time window 7:30-16:40
     * Deletes any Appointments previously in the database
     *
     * @param startDate The start date. Must be a Monday
     * @throws InvalidAppointmentStateException if the start date is not a Monday or an Invalid Appointment is constructed
     * @throws RepositoryConnectionException    if the connection to the repository fails
     * @throws SQLException                     if an SQL error occurs
     */
    public static void generate(LocalDate startDate) throws InvalidAppointmentStateException, RepositoryConnectionException, SQLException {
        try {
            if (!(startDate.getDayOfWeek() == DayOfWeek.MONDAY))
                throw new InvalidAppointmentStateException();

            DBRepository repository = DBRepository.getInstance();

            repository.deleteAllAppointments();

            final LocalTime start = LocalTime.of(7, 30);
            final LocalTime end = LocalTime.of(16, 40);
            final TimeWindow timeWindow = new TimeWindow(start, end);

            final String note = null;
            final State state = State.FREE;

            for (int week = 0; week < 3; week++) {
                for (int day = 0; day < 5; day++) {
                    LocalDate date = startDate.plusDays(7 * week + day);

                    Appointment appointment = new Appointment(date, timeWindow, null, state);
                    repository.insertAppointment(appointment);
                }
            }
        } catch (InvalidTimeWindowException e) {
            //Won't happen because 16:40 is after 8:30
        }
    }

    /**
     * Gets all appointments from the database
     *
     * @return A list of all appointments
     * @throws RepositoryConnectionException    if the connection to the repository failed
     * @throws InvalidAppointmentStateException if the data from the database is invalid
     * @throws InvalidTimeWindowException       if an appointment in the database has an invalid time window
     * @throws SQLException                     if an SQL error occurs
     */
    public static List<Appointment> all() throws RepositoryConnectionException,
            InvalidAppointmentStateException, SQLException, InvalidTimeWindowException {
        DBRepository repository = DBRepository.getInstance();

        return repository.getAppointments();
    }
}

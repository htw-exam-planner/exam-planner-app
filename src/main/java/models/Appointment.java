package models;

import repository.DBRepository;

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
    private Group group=null;
    private LocalTime bookingStart=null;
    private LocalTime bookingEnd=null;
    private String room = null;

    private Appointment(LocalDate date, LocalTime timeWindowStart, LocalTime timeWindowEnd, String note) {
        this.date = date;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
        this.note = note;
    }

    /**
     * Constructs a new free or deactivated appointment
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

        if(!(state==State.FREE || state==State.DEACTIVATED)) {
            throw new InvalidAppointmentStateException();
        }
        this.state = state;
    }

    /**
     * Constructs a new appointment reserved by a group
     * @param date The appointment's date
     * @param timeWindowStart The start of the appointment's time window
     * @param timeWindowEnd The end of the appointment's time window
     * @param note The appointment's note
     * @param state The appointment's state
     * @param group The group that reserved the appointment
     * @throws InvalidAppointmentStateException if the state is not State.RESERVED
     */
    public Appointment(LocalDate date, LocalTime timeWindowStart, LocalTime timeWindowEnd,
                       String note, State state, Group group) throws InvalidAppointmentStateException {

        this(date,timeWindowStart,timeWindowEnd,note);

        if(state!=State.RESERVED){
            throw new InvalidAppointmentStateException();
        }
        this.state = state;
        this.group = group;
    }

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

        this(date,timeWindowStart,timeWindowEnd,note);

        if(state!=State.BOOKED){
            throw new InvalidAppointmentStateException();
        }
        this.state = state;
        this.group = group;
        this.bookingStart = bookingStart;
        this.bookingEnd = bookingEnd;
        this.room = room;
    }

    /**
     * Gets all appointments from the database
     * @return A list of all appointments
     * @throws Exception if an exception occurs while getting the appointments
     */
    public static List<Appointment> all() throws Exception {
        try {
            DBRepository repository = DBRepository.getInstance();

            return repository.getAppointments();
        }
        catch (Exception e){
            throw new Exception("Exception while getting appointments from database");
        }
    }
}
package models;

import repository.DBRepository;
import repository.RepositoryConnectionException;

import java.sql.SQLException;

public class Booking {
    private Group group;
    private TimeWindow timeWindow;
    private String room;

    /**
     * Constructs a new Booking
     * @param group the Booking's group
     * @param timeWindow the Booking's time window
     * @param room the Booking's room
     */
    public Booking(Group group, TimeWindow timeWindow, String room) {
        this.group = group;
        this.timeWindow = timeWindow;
        this.room = room;
    }

    /**
     * Gets the Booking's group
     * @return the group that booked the appointment
     */
    public Group getGroup() {
        return group;
    }

    /**
     * Gets the booking's time window
     * @return the time window
     */
    public TimeWindow getTimeWindow() {
        return timeWindow;
    }

    /**
     * Gets the Booking's room
     * @return the room
     */
    public String getRoom() {
        return room;
    }

    /**
     * Sets the booking's time window
     * @param timeWindow the time window
     */
    public void updateTimeWindow(TimeWindow timeWindow) throws RepositoryConnectionException, SQLException {
        this.timeWindow = timeWindow;
        DBRepository.getInstance().updateBooking(this);
    }

    /**
     * sets the booking's room
     * @param room the room
     */
    public void updateRoom(String room) throws RepositoryConnectionException, SQLException {
        this.room = room;
        DBRepository.getInstance().updateBooking(this);
    }

    /**
     * Makes a string with the Booking's group, TimeWindow and Room (if applicable)
     * @return a string with the Booking's group, TimeWindow and Room (if applicable)
     */
    @Override
    public String toString() {
        if (room==null)
            return group.toString() + ", " + timeWindow.toString();

        return group.toString() + ", " + timeWindow.toString() + ", " + room;
    }
}

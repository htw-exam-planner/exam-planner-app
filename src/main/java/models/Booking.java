package models;

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
    public void setTimeWindow(TimeWindow timeWindow) {
        this.timeWindow = timeWindow;
    }

    /**
     * sets the booking's room
     * @param room the room
     */
    public void setRoom(String room) {
        this.room = room;
    }
}

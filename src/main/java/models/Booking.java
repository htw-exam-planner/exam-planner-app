package models;

import java.time.LocalTime;

public class Booking {
    private Group group;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;

    public Booking(Group group, LocalTime startTime, LocalTime endTime, String room) {
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    public Group getGroup() {
        return group;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public String getRoom() {
        return room;
    }

    /**
     * Sets the booking's start time
     * @param startTime the start time of the booking
     * @throws InvalidAppointmentStateException if the start time is after the Booking's end time (if set)
     */
    public void setStartTime(LocalTime startTime) throws InvalidAppointmentStateException {
        if(endTime!=null){
            if(startTime.isAfter(endTime))
                throw new InvalidAppointmentStateException();
        }

        this.startTime = startTime;
    }

    /**
     * Sets the Booking's end time
     * @param endTime the end time of the Booking
     * @throws InvalidAppointmentStateException if the end time is before the start time
     */
    public void setEndTime(LocalTime endTime) throws InvalidAppointmentStateException {
        if(endTime.isBefore(startTime))
            throw new InvalidAppointmentStateException();

        this.endTime = endTime;
    }

    public void setRoom(String room) {
        this.room = room;
    }
}

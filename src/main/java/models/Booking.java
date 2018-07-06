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


}

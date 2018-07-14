package models;

import java.time.LocalTime;
import java.util.Objects;

public class TimeWindow {
    private LocalTime start;
    private LocalTime end;

    /**
     * Constructs a new TimeWindow
     * @param start the start time
     * @param end the end time
     * @throws InvalidTimeWindowException if the end is before the start
     */
    public TimeWindow(LocalTime start, LocalTime end) throws InvalidTimeWindowException {
        if(end!=null && end.isBefore(start))
            throw new InvalidTimeWindowException();

        this.start = start;
        this.end = end;
    }

    /**
     * Constructs a new TimeWindow without end time
     * @param start the start time
     */
    public TimeWindow(LocalTime start){
        this.start = start;
        this.end=null;
    }

    /**
     * Gets the start time
     * @return the start time
     */
    public LocalTime getStart() {
        return start;
    }

    /**
     * Gets the end time
     * @return the end time
     */
    public LocalTime getEnd() {
        return end;
    }

    /**
     * Sets the start time
     * @param start the start time
     * @throws InvalidTimeWindowException if the start time is after the end time
     */
    public void setStart(LocalTime start) throws InvalidTimeWindowException {
        if(end!=null && start.isAfter(end))
            throw new InvalidTimeWindowException();

        this.start = start;
    }

    /**
     * Sets the end time
     * @param end the end time
     * @throws InvalidTimeWindowException if the end time is before the start time
     */
    public void setEnd(LocalTime end) throws InvalidTimeWindowException {
        if(end.isBefore(start))
            throw new InvalidTimeWindowException();
        this.end = end;
    }

    /**
     * Checks if the TimeWindow contains the time time
     * @param time the time to be checked
     * @return true if time is after start and before end (if present), false otherwise
     */
    boolean contains(LocalTime time){
        if(end==null)
            return time.isAfter(start);

        return (time.isAfter(start) && time.isBefore(end));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TimeWindow)) return false;
        TimeWindow that = (TimeWindow) o;
        return Objects.equals(start, that.start) &&
                Objects.equals(end, that.end);
    }

    /**
     * Makes a string of the time window
     * @return if the end time is set "start-end", else only start
     */
    @Override
    public String toString(){
        if(end==null) return start.toString();

        return start.toString() + "-" + end.toString();
    }
}

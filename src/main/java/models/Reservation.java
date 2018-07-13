package models;

public class Reservation {
    private Group group;

    /**
     * Constructs a new Reservation for the Group group
     * @param group the Group reserving the Appointment
     */
    public Reservation(Group group) {
        this.group = group;
    }

    /**
     * Gets the Reservation's group
     * @return the Reservation's group
     */
    public Group getGroup() {
        return group;
    }
}

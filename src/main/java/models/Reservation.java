package models;

public class Reservation {
    private Group group;

    public Reservation(Group group) {
        this.group = group;
    }

    public Group getGroup() {
        return group;
    }
}

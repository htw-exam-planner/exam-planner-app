package models;

import java.util.*;

public class Group {
    private int groupNumber;

    private static List<Group> groups = new ArrayList<Group>();

    public Group(int groupNumber){
        this.groupNumber = groupNumber;
    }

    /**
     * Gets all groups
     * @return a list of all groups
     */
    public static List<Group> all(){
        groups.clear();
        groups.add(new Group(1));
        groups.add(new Group(2));
        groups.add(new Group(3));
        return groups;
    }

    /**
     * convert a Group to String
     * @return Group name as "Gruppe &lt;number&gt;"
     */
    @Override
    public String toString() {
        return "Gruppe "+groupNumber;
    }
}

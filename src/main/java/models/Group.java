package models;

import repository.DBRepository;

import java.util.*;

public class Group {
    private int groupNumber;

    /**
     * Constructs a new group
     * @param groupNumber Number of the group
     */
    public Group(int groupNumber){
        this.groupNumber = groupNumber;
    }

    /**
     * Gets all groups
     * @return a list of all groups
     * @throws Exception if an exception is thrown while getting the groups
     */
    public static List<Group> all() throws Exception {
        DBRepository repository;
        List<Group> groups;
        try {
            repository = DBRepository.getInstance();

            groups = repository.getGroups();
        } catch (Exception e){
            e.printStackTrace();
            throw new Exception("Exception while getting groups from database");
        }

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

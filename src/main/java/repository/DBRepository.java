package repository;

import models.Group;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBRepository {
    private static DBRepository instance = null;

    private Connection connection;

    /**
     * Gets the singleton instance of DBRepository
     * @return singleton instance of DBRepository
     * @throws SQLException if the connection fails
     * @throws ClassNotFoundException if the JDBC driver class can't be found
     * @throws IOException if the configuration file can't be loaded
     */
    public static DBRepository getInstance() throws SQLException, ClassNotFoundException, IOException{
        if(instance==null){
            instance = new DBRepository();
        }
        return instance;
    }

    /**
     * Connects to the database as specified in the db.properties
     * @throws SQLException if the connection fails
     * @throws ClassNotFoundException if the JDBC driver class can't be found
     * @throws IOException if the configuration file can't be loaded
     */
    private DBRepository() throws SQLException, ClassNotFoundException, IOException {
        Properties properties = new Properties();
        InputStream propertiesFile = getClass().getResourceAsStream("/db.properties");
        properties.load(propertiesFile);

        Class.forName(properties.getProperty("DB_DRIVER_CLASS"));

        connection = DriverManager.getConnection(
                properties.getProperty("DB_URL"),
                properties.getProperty("DB_USERNAME"),
                properties.getProperty("DB_PASSWORD")
        );
    }

    /**
     * Gets all groups from the database
     * @return A list of all groups
     * @throws SQLException if SQL Execution fails
     */
    public List<Group> getGroups() throws SQLException {
        final String query = "SELECT GroupNumber FROM Groups";
        Statement statement = connection.createStatement();

        ResultSet results = statement.executeQuery(query);

        ArrayList<Group> groups = new ArrayList<Group>();

        while (results.next()){
            int groupNumber = results.getInt("GroupNumber");

            Group group = new Group(groupNumber);

            groups.add(group);
        }

        return groups;
    }
}

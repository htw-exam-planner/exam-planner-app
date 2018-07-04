package repository;

import models.Appointment;
import models.Group;
import models.InvalidAppointmentStateException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

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
        propertiesFile.close();

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

        ArrayList<Group> groups = new ArrayList<>();

        while (results.next()){
            int groupNumber = results.getInt("GroupNumber");

            Group group = new Group(groupNumber);

            groups.add(group);
        }

        return groups;
    }



    /**
     * Gets all appintments from the database
     * @return A list of all appointments
     * @throws SQLException if SQL execution fails
     * @throws InvalidAppointmentStateException if an appointment has an invalid state
     */
    public List<Appointment> getAppointments() throws SQLException, InvalidAppointmentStateException {
        final String query ="SELECT A.Date, A.Activated, A.StartTime, A.EndTime, A.Note,\n" +
                "R.Groups, B.StartTime AS BookStart, B.EndTime AS BookEnd, B.Room\n" +
                "FROM Appointment A\n" +
                "LEFT JOIN Reservation R on A.Date = R.Appointment\n" +
                "LEFT JOIN Booking B on R.Groups = B.Reservation;";
        Statement statement = connection.createStatement();

        ResultSet results = statement.executeQuery(query);

        ArrayList<Appointment> appointments = new ArrayList<>();

        while (results.next()){
            LocalDate date = results.getDate("Date").toLocalDate();
            boolean active = results.getBoolean("Activated");
            LocalTime startTime = results.getTime("StartTime").toLocalTime();
            LocalTime endTime = results.getTime("EndTime").toLocalTime();
            String note = results.getString("Note");

            int groupNo = results.getInt("Groups");
            boolean hasReservation = !results.wasNull();
            Group group = (results.wasNull()) ? null : new Group(groupNo);

            Time bookStartSQL = results.getTime("BookStart");
            boolean hasBooking = !results.wasNull();
            LocalTime bookingStart = results.wasNull() ? null : bookStartSQL.toLocalTime();

            Time bookEndSQL = results.getTime("BookEnd");
            LocalTime bookingEnd = (results.wasNull()) ? null : bookEndSQL.toLocalTime();

            String room = results.getString("Room");

            Appointment.State state;

            if(!active) {
                state= Appointment.State.DEACTIVATED;
            }
            else{
                if(hasReservation){
                    if(hasBooking)
                        state = Appointment.State.BOOKED;
                    else
                        state = Appointment.State.RESERVED;
                }
                else
                    state = Appointment.State.FREE;
            }

            Appointment appointment = new Appointment(date,startTime,endTime,note,state,group,bookingStart,bookingEnd,room);

            appointments.add(appointment);
        }

        return appointments;
    }
}

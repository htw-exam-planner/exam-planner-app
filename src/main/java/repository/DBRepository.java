package repository;

import models.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class DBRepository {
    private static DBRepository instance = null;

    private Connection connection;

    /**
     * Gets the singleton instance of DBRepository
     * @return singleton instance of DBRepository
     * @throws RepositoryConnectionException if the connection to the database fails
     */
    public static DBRepository getInstance() throws RepositoryConnectionException{
        try {
            if(instance==null){
                instance = new DBRepository();
            }
            return instance;
        } catch (Exception e){
            throw new RepositoryConnectionException();
        }
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

            Appointment appointment;

            if(!active) {
                state= Appointment.State.DEACTIVATED;
                appointment = new Appointment(date,startTime,endTime,note,state);
            }
            else{
                if(hasReservation){
                    if(hasBooking){
                        state = Appointment.State.BOOKED;
                        Reservation reservation = new Reservation(group);
                        appointment = new Appointment(date,startTime,endTime,note,state,reservation);
                    }
                    else {
                        state = Appointment.State.RESERVED;
                        Booking booking = new Booking(group,bookingStart,bookingEnd,room);
                        appointment = new Appointment(date,startTime,endTime,note,state,booking);
                    }
                }
                else{
                    state = Appointment.State.FREE;
                    appointment = new Appointment(date,startTime,endTime,note,state);
                }
            }

            appointments.add(appointment);
        }

        return appointments;
    }

    private void deleteAppointment(Appointment appointment) throws SQLException {
        String query = "DELETE FROM Appointment WHERE Date = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setDate(1,Date.valueOf(appointment.getDate()));

        statement.execute();
    }

    private void insertReservation(Group group, Date date) throws SQLException {
        String query = "INSERT INTO Reservation (Groups, Appointment) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1,group.getGroupNumber());
        statement.setDate(2,date);

        statement.execute();
    }

    private void insertBooking(Group group, Time startTime, Time endTime, String room) throws SQLException {
        String query = "INSERT INTO Booking (Reservation, StartTime, EndTime, Room) VALUES (?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1,group.getGroupNumber());
        statement.setTime(2,startTime);
        statement.setTime(3,endTime);
        statement.setString(4,room);

        statement.execute();
    }

    private void insertAppointment(Appointment appointment) throws SQLException {
        String query = "INSERT INTO Appointment (Date, Activated, StartTime, EndTime, Note) VALUES (?, ?, ?, ?, ? );";
        PreparedStatement statement=connection.prepareStatement(query);

        statement.setDate(1,Date.valueOf(appointment.getDate()));
        statement.setBoolean(2,(appointment.getState() != Appointment.State.DEACTIVATED));
        statement.setTime(3,Time.valueOf(appointment.getTimeWindowStart()));
        statement.setTime(4,Time.valueOf(appointment.getTimeWindowEnd()));
        statement.setString(5,appointment.getNote());

        statement.execute();

        if(appointment.getState() == Appointment.State.RESERVED){
            insertReservation(appointment.getReservation().getGroup(),Date.valueOf(appointment.getDate()));
        }

        if(appointment.getState() == Appointment.State.BOOKED){
            insertReservation(appointment.getBooking().getGroup(),Date.valueOf(appointment.getDate()));
            insertBooking(appointment.getBooking().getGroup(),
                    Time.valueOf(appointment.getBooking().getStartTime()),
                    Time.valueOf(appointment.getBooking().getEndTime()),
                    appointment.getBooking().getRoom());
        }
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        deleteAppointment(appointment);
        insertAppointment(appointment);
    }


}

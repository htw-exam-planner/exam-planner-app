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
    public static DBRepository getInstance() throws RepositoryConnectionException {
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
     * @throws InvalidTimeWindowException if an appointment's or booking's time window is invalid
     */
    public List<Appointment> getAppointments() throws SQLException, InvalidAppointmentStateException, InvalidTimeWindowException {
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
            TimeWindow timeWindow = new TimeWindow(startTime,endTime);
            String note = results.getString("Note");

            int groupNo = results.getInt("Groups");
            boolean hasReservation = !results.wasNull();
            Group group = (results.wasNull()) ? null : new Group(groupNo);

            Time bookStartSQL = results.getTime("BookStart");
            boolean hasBooking = !results.wasNull();
            LocalTime bookingStart = results.wasNull() ? null : bookStartSQL.toLocalTime();

            Time bookEndSQL = results.getTime("BookEnd");
            LocalTime bookingEnd = (results.wasNull()) ? null : bookEndSQL.toLocalTime();

            TimeWindow bookingWindow = new TimeWindow(bookingStart,bookingEnd);

            String room = results.getString("Room");

            Appointment.State state;

            Appointment appointment;

            if(!active) {
                state= Appointment.State.DEACTIVATED;
                appointment = new Appointment(date,timeWindow,note,state);
            }
            else{
                if(hasReservation){
                    if(hasBooking){
                        state = Appointment.State.BOOKED;
                        Booking booking = new Booking(group,bookingWindow,room);
                        appointment = new Appointment(date,timeWindow,note,state,booking);
                    }
                    else {
                        state = Appointment.State.RESERVED;
                        Reservation reservation = new Reservation(group);
                        appointment = new Appointment(date,timeWindow,note,state,reservation);
                    }
                }
                else{
                    state = Appointment.State.FREE;
                    appointment = new Appointment(date,timeWindow,note,state);
                }
            }

            appointments.add(appointment);
        }

        return appointments;
    }

    /**
     * Deletes the an appointment and, if present, its reservation or booking
     * @param appointment the appointment to be deleted from the database
     * @throws SQLException if an SQL error occurs
     */
    private void deleteAppointment(Appointment appointment) throws SQLException {
        String query = "DELETE FROM Appointment WHERE Date = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setDate(1,Date.valueOf(appointment.getDate()));

        statement.execute();
    }

    /**
     * Deletes a single Group from the database
     * @param group the group to be deleted
     * @throws SQLException if an SQL error occurs
     */
    public void deleteGroup(Group group) throws SQLException {
        String query = "DELETE FROM Groups WHERE GroupNumber = ?";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1,group.getNumber());

        statement.execute();
    }

    /**
     * Deletes all Appointments from the database, including their Reservations and Bookings
     * @throws SQLException if an SQL error occurs
     */
    public void deleteAllAppointments() throws SQLException {
        String query = "DELETE FROM Appointment;";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.execute();
    }

    /**
     * Deletes all Groups from the database
     * @throws SQLException if an SQL error occurs
     */
    public void deleteAllGroups() throws SQLException {
        String query ="DELETE FROM Groups;";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.execute();
    }

    /**
     * Insert into table Reservation
     * @param group the group to be inserted
     * @param date the date to be inserted
     * @throws SQLException if an SQL error occurs
     */
    private void insertReservation(Group group, Date date) throws SQLException {
        String query = "INSERT INTO Reservation (Groups, Appointment) VALUES (?, ?)";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1,group.getNumber());
        statement.setDate(2,date);

        statement.execute();
    }

    /**
     * Inserts into table Booking
     * @param group the group to be inserted
     * @param startTime the start time of the booking
     * @param endTime the end time of the booking
     * @param room the room of the booking
     * @throws SQLException if an SQL error occurs
     */
    private void insertBooking(Group group, Time startTime, Time endTime, String room) throws SQLException {
        String query = "INSERT INTO Booking (Reservation, StartTime, EndTime, Room) VALUES (?, ?, ?, ?);";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1,group.getNumber());
        statement.setTime(2,startTime);
        statement.setTime(3,endTime);
        statement.setString(4,room);

        statement.execute();
    }

    /**
     * Inserts into the table Appointment, and, if applicable, into Reservation and Booking as well
     * @param appointment the Appointment to be inserted
     * @throws SQLException if an SQL error occurs
     */
    public void insertAppointment(Appointment appointment) throws SQLException {
        String query = "INSERT INTO Appointment (Date, Activated, StartTime, EndTime, Note) VALUES (?, ?, ?, ?, ? );";
        PreparedStatement statement=connection.prepareStatement(query);

        statement.setDate(1,Date.valueOf(appointment.getDate()));
        statement.setBoolean(2,(appointment.getState() != Appointment.State.DEACTIVATED));
        statement.setTime(3,Time.valueOf(appointment.getTimeWindow().getStart()));
        statement.setTime(4,Time.valueOf(appointment.getTimeWindow().getEnd()));
        statement.setString(5,appointment.getNote());

        statement.execute();

        if(appointment.getState() == Appointment.State.RESERVED){
            insertReservation(appointment.getReservation().getGroup(),Date.valueOf(appointment.getDate()));
        }

        if(appointment.getState() == Appointment.State.BOOKED){
            TimeWindow timeWindow = appointment.getBooking().getTimeWindow();
            Time startTime = Time.valueOf(timeWindow.getStart());
            Time endTime = timeWindow.getEnd() == null ? null : Time.valueOf(timeWindow.getEnd());

            insertReservation(appointment.getBooking().getGroup(),Date.valueOf(appointment.getDate()));
            insertBooking(appointment.getBooking().getGroup(),
                    startTime,
                    endTime,
                    appointment.getBooking().getRoom());
        }
    }

    /**
     * Inserts a single group into the database
     * @param group the group to be inserted
     * @throws SQLException if an SQL error occurs
     */
    public void insertGroup(Group group) throws SQLException {
        String query = "INSERT INTO Groups (GroupNumber) VALUES (?);";
        PreparedStatement statement = connection.prepareStatement(query);

        statement.setInt(1,group.getNumber());

        statement.execute();
    }

    /**
     * Updates an Appointment, and, if applicable, its Reservation and Booking
     * by deleting the old data and inserting the new data
     * @param appointment The Appointment to be updated
     * @throws SQLException if an SQL error occurs
     */
    public void updateAppointment(Appointment appointment) throws SQLException {
        deleteAppointment(appointment);
        insertAppointment(appointment);
    }


}

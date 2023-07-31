package comp3350.plarty.persistence;

import org.joda.time.DateTime;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLWarning;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;

public class DataAccessObject implements DataAccess {

    private Statement statement;
    private Connection connection;
    private String dbType;
    private int mainUserId;
    private static int userID, eventCount;
    private String cmdString;
    private int updateCount;

    public DataAccessObject(){ }

    public void open(String dbPath) {
        try {
            mainUserId = 0;
            dbType = "HSQL";
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            String url = "jdbc:hsqldb:file:"+dbPath; // stored on disk mode
            connection = DriverManager.getConnection(url, "SA", "");
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS WHERE USERID = "+mainUserId);
            if(!resultSet.next()) {
                initialize();
            }
            resultSet.close();
        } catch (Exception e) {
            processSQLError(e);
        }
        System.out.println("Opened " +dbType +" database " +" database/Plarty");
    }

    /**
     * If the database is empty, we re-populate it with some default data.
     */
    private void initialize() {
        try {
            userID = 0;
            GenerateDataUtil generateDataUtil = new GenerateDataUtil();
            generateDataUtil.populateData(this);
        } catch (Exception e) {
            processSQLError(e);
        }
    }

    public void close() {
        try {
            cmdString = "DELETE FROM USERS";
            statement.executeQuery(cmdString);
            cmdString = "DELETE FROM EVENT";
            statement.executeQuery(cmdString);
            cmdString = "DELETE FROM INVITE";
            statement.executeQuery(cmdString);
            cmdString = "DELETE FROM INACTIVE";
            statement.executeQuery(cmdString);
            cmdString = "shutdown compact";
            statement.executeQuery(cmdString);
            connection.close();
            eventCount = 0;
        } catch(Exception e) {
            processSQLError(e);
        }
        System.out.println("Closed " +"HSQL" +" database Plarty");
    }

    public boolean createEvent(Event event) {
        String values;
        try {
            String daysStored = "null";
            if(event instanceof RecurringEventGenerator)
                daysStored = convertDaysToString((RecurringEventGenerator)event);
            values = event.getId() + ", '"+ event.getName() + "' ," + event.getOrganiserId() + ", '" + event.getLocation() + "' , '" + event.getStart().getMillis() + "' , '" + event.getEnd().getMillis() + "' , '" + daysStored + "'";
            cmdString = "Insert into EVENT "+ "Values (" + values + ")";
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
            eventCount++;
        } catch(Exception e) {
            processSQLError(e);
        }
        return true;
    }

    /**
     * Recurring event generators store their weekdays as strings of length 7
     * or shorter (each digit represents a day). This class takes an event's
     * HashSet of weekdays and converts it to a string.
     *
     * @param event     the RecurringEventGenerator whose information to use
     * @return          a string containing the Integer weekdays the event
     *                  occurs on
     */
    private String convertDaysToString(RecurringEventGenerator event){
        StringBuilder dayValues = new StringBuilder();
        for(int day : event.getDaysOfWeek())
            dayValues.append(day);
        return dayValues.toString();
    }

    public int assignEventId() {
        int max = eventCount;
        try {
            cmdString = "SELECT * FROM EVENT";
            ResultSet resultSet = statement.executeQuery(cmdString);

            while(resultSet.next()) {
                int id = resultSet.getInt("EVENTID");
                if(id >= max)
                    max = id + 1;
            }
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }
        return max;
    }

    public Event getEvent(int eventId){
        Event newEvent = null;
        String eventName = "", userName = "", location = "";
        int organiserId = -1;
        User user;
        long startMillis = 0, endMillis = 0;
        String weekdays = "";

        try {
            cmdString = "Select * from EVENT where EVENTID=" +eventId;
            ResultSet resultSet = statement.executeQuery(cmdString);
            if(resultSet.next()) {
                organiserId = resultSet.getInt("ORGANIZERID");
                eventName = resultSet.getString("NAME");
                location = resultSet.getString("LOCATION");
                startMillis = resultSet.getLong("STARTTIME");
                endMillis = resultSet.getLong("ENDTIME");
                weekdays = resultSet.getString("WEEKDAYS");
            }
            resultSet.close();
            cmdString = "Select * from USERS where USERID = "+organiserId;
            System.out.println("Checking for user of id: " + organiserId);
            resultSet = statement.executeQuery(cmdString);
            while(resultSet.next()) {
                userName = resultSet.getString("NAME");
            }
            user = new User(userName, organiserId);
            newEvent = createEventObject(eventName, eventId, user, location, startMillis, endMillis, weekdays);
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }
        return newEvent;
    }

    public ArrayList<Event> getOwnedEvents(int userId) {
        ArrayList<Event> result = new ArrayList<>();
        try {
            cmdString = "Select * from USERS where USERID = " + userId;
            ResultSet resultSet = statement.executeQuery(cmdString);
            String weekdays;
            String userName = "";
            while(resultSet.next()) {
                userName = resultSet.getString("NAME");
            }
            resultSet.close();
            User organiser = new User(userName, userId);
            resultSet.close();
            cmdString = "Select * from EVENT where EVENT.ORGANIZERID=" + userId;
            resultSet = statement.executeQuery(cmdString);
            while(resultSet.next()) {
                int id = resultSet.getInt("EVENTID");
                String eventName = resultSet.getString("NAME");
                String location = resultSet.getString("LOCATION");
                long startMillis = resultSet.getLong("STARTTIME");
                long endMillis = resultSet.getLong("ENDTIME");
                weekdays = resultSet.getString("WEEKDAYS");
                Event event = createEventObject(eventName, id, organiser, location, startMillis, endMillis, weekdays);
                result.add(event);
            }
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }
        return result;
    }

    /**
     * Constructs an Event object (Single or Recurring Generator) to return on
     * a "get" request.
     *
     * @param eventName         the name retrieved from the DB
     * @param eventId           the event ID from the DB
     * @param user              the organiser, its name and ID are
     *                          both from the DB
     * @param location          the location from the DB
     * @param start             the start time, in millis, from the DB
     * @param end               the end time, in millis, from the DB
     * @param weekdays          the string of weekdays from the DB
     *                          a null value indicates a single event
     * @return                  the event object created with this metadata
     */
    private Event createEventObject(String eventName, int eventId, User user, String location, long start, long end, String weekdays){
        Event newEvent;
        if(weekdays.equals("null"))
            newEvent = new SingleEvent(eventName, eventId, user, location, new DateTime(start), new DateTime(end));
        else{
            HashSet<Integer> days = new HashSet<>();
            for(int i=0; i<weekdays.length(); i++) {
                days.add(weekdays.charAt(i) - '0');
            }
            newEvent = new RecurringEventGenerator(eventName, eventId, user, location, new DateTime(start), new DateTime(end), days);
        }
        return newEvent;
    }

    public boolean updateEvent(Event event) {
        String values;
        String where;
        boolean success = false;
        try {
            // Should check for empty values and not update them
            String weekdays = "null";
            if(event instanceof RecurringEventGenerator){
                weekdays = convertDaysToString((RecurringEventGenerator)event);
            }
            values = "NAME='" +event.getName() +
                    "', ORGANIZERID=" + event.getOrganiser().getId() +
                    ", LOCATION='" + event.getLocation() +
                    "', STARTTIME=" + event.getStart().getMillis()+
                    ", ENDTIME=" + event.getEnd().getMillis()+
                    ", WEEKDAYS='" + weekdays+"'";
            where = "where EVENTID=" +event.getId();
            cmdString = "Update EVENT " +" Set " +values +" " +where;
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
            success = true;
        } catch(Exception e) {
            processSQLError(e);
        }
        return success;
    }

    public boolean deleteEvent(int eventId) {
        boolean success = true;
        try {
            cmdString = "Delete from EVENT where EVENTID=" + eventId;
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
        } catch(Exception e) {
            success = false;
            processSQLError(e);
        }
        return success;
    }

    public User createUser(String name){
        User user = null;
        try {
            user = new User(name,userID);
            String values = userID + ", '" + name + "'";
            userID++;
            cmdString = " Insert into USERS " + "Values( " + values + ")";
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
        } catch(Exception e) {
            processSQLError(e);
        }
        return user;
    }

    public User getUser(int id){
        User user = null;
        try {
            cmdString = "Select * from USERS where USERID = " +id;
            ResultSet resultSet = statement.executeQuery(cmdString);
            while(resultSet.next()) {
                String name = resultSet.getString("NAME");
                user = new User(name, id);
            }
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }
        return user;
    }

    public User getMainUser() {
        User user = null;
        int id = -1;
        String name = "";
        try {
            String cmdString = "Select * from USERS where USERID = " + mainUserId;
            ResultSet resultSet = statement.executeQuery(cmdString);
            while(resultSet.next()) {
                id = resultSet.getInt("USERID");
                name = resultSet.getString("NAME");
            }
            user = new User(name,id);
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }
        return user;
    }

    public ArrayList<User> getAllOtherUsers(int id) {
        ArrayList<User> result = new ArrayList<>();
        try {
            cmdString = "SELECT * FROM USERS WHERE USERID != "+id;
            ResultSet resultSet = statement.executeQuery(cmdString);
            while(resultSet.next()) {
                int userId = resultSet.getInt("USERID");
                String name = resultSet.getString("NAME");
                User user = new User(name, userId);
                result.add(user);
            }
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }
        return result;
    }

    public boolean updateUser(User user){
        String values;
        String where;
        boolean success = false;

        try {
            // Should check for empty values and not update them
            values = "NAME='" +user.getName()
                    +"'";
            where = "where USERID=" +user.getId();
            cmdString = " Update USERS " +" Set " +values +" " +where;
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
            success = true;
        } catch (Exception e) {
            processSQLError(e);
        }
        return success;
    }

    public boolean createInvite(int eventId, int userId){
        boolean result = false;
        try {
            String values = eventId + ", " + userId + ", 3";
            cmdString = "Insert into INVITE " + "Values(" + values + ")";
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
            result = true;
        } catch(Exception e) {
            processSQLError(e);
        }
        return result;
    }

    public InviteResponse getInvite(int eventId, int userId){
        InviteResponse response = null;
        try {
            cmdString = "Select * from INVITE where EVENTID=" + eventId + " AND USERID=" + userId;
            ResultSet resultSet = statement.executeQuery(cmdString);
            checkWarning(statement, updateCount);
            while(resultSet.next()) {
                response = InviteResponse.values()[resultSet.getInt("RESPONSE")];
            }
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }
        return response;
    }

    public boolean deleteInvite(int eventId, int userId){
        boolean success = false;
        try {
            cmdString = "Delete from INVITE where EVENTID=" + eventId + " AND USERID=" + userId;
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
            success = true;
        } catch(Exception e) {
            processSQLError(e);
        }
        return success;
    }

    public HashMap<Event, InviteResponse> getInvitations(int userId) {
        HashMap<Event, InviteResponse> result = new HashMap<>();
        try {
            cmdString = "SELECT INVITE.*,EVENT.*,USERS.NAME AS USERNAME FROM INVITE JOIN EVENT ON INVITE.EVENTID = EVENT.EVENTID " +
                    " JOIN USERS ON ORGANIZERID = USERS.USERID" + " WHERE INVITE.USERID=" + userId;
            ResultSet resultSet = statement.executeQuery(cmdString);

            while(resultSet.next()) {
                int eventId = resultSet.getInt("EVENTID");
                int response = resultSet.getInt("RESPONSE");
                String eventName = resultSet.getString("NAME");
                int organiserId = resultSet.getInt("ORGANIZERID");
                String location = resultSet.getString("LOCATION");
                long startMillis = resultSet.getLong("STARTTIME");
                long endMillis = resultSet.getLong("ENDTIME");
                String userName = resultSet.getString("USERNAME");

                User organiser = new User(userName, organiserId);
                Event event = new SingleEvent(eventName, eventId, organiser, location, new DateTime(startMillis), new DateTime(endMillis));
                result.put(event, InviteResponse.values()[response]);
            }
        } catch(Exception e) {
            processSQLError(e);
        }
        return result;
    }

    public boolean updateInvite(int eventId, int userId, InviteResponse response) {
        boolean success = false;
        int responseCode = response.ordinal();
        try {
            cmdString = "UPDATE INVITE SET RESPONSE=" + responseCode +
                    " WHERE EVENTID = " + eventId +
                    " AND USERID = " + userId;
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
            success = true;
        } catch(Exception e) {
            processSQLError(e);
        }
        return success;
    }

    public HashMap<User, InviteResponse> getInvitedUsers(SingleEvent event) {
        HashMap<User, InviteResponse> result = new HashMap<>();
        try {
            cmdString = "SELECT INVITE.*,USERS.NAME AS USERNAME FROM INVITE JOIN USERS ON INVITE.USERID = USERS.USERID " +
                    " WHERE INVITE.EVENTID=" + event.getId();
            ResultSet resultSet = statement.executeQuery(cmdString);

            while(resultSet.next()) {
                int response = resultSet.getInt("RESPONSE");
                int userId = resultSet.getInt("USERID");
                String userName = resultSet.getString("USERNAME");

                User user = new User(userName, userId);
                result.put(user, InviteResponse.values()[response]);
            }
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }
        return result;
    }

    public boolean createInactive(int eventId, int userId) {
        boolean result = false;
        try {
            cmdString = "SELECT INACTIVE.* FROM INACTIVE WHERE INACTIVE.USERID=" + userId;
            ResultSet resultSet = statement.executeQuery(cmdString);
            while(resultSet.next()) { // delete previous inactive
                int prevEventId = resultSet.getInt("EVENTID");
                System.out.println("Previous Inactive Event: " + prevEventId);
                cmdString = "DELETE FROM INACTIVE WHERE INACTIVE.EVENTID="+prevEventId;
                statement.executeQuery(cmdString);
                cmdString = "DELETE FROM EVENT WHERE EVENT.EVENTID="+prevEventId;
                statement.executeQuery(cmdString);
            }
            String values = eventId + ", " + userId;
            cmdString = "Insert into INACTIVE Values(" + values + ")";
            updateCount = statement.executeUpdate(cmdString);
            checkWarning(statement, updateCount);
            System.out.println("Finished CreateInactive");
            result = true;
        } catch(Exception e) {
            processSQLError(e);
        }
        return result;
    }

    public Event getInactive(int userId) {
        Event event = null;
        try {
            cmdString = "Select * from INACTIVE where USERID=" + userId;
            ResultSet resultSet = statement.executeQuery(cmdString);
            if(resultSet.next()) {
                int eventId = resultSet.getInt("EVENTID");
                event = getEvent(eventId);
            }
            resultSet.close();
        } catch(Exception e) {
            processSQLError(e);
        }

        return event;
    }

    public void processSQLError(Exception e) {
        String result = "*** SQL Error: " + e.getMessage();
        e.printStackTrace();
        System.out.println(result);
    }

    public void checkWarning(Statement statement, int updateCount) {
        try {
            SQLWarning warning = statement.getWarnings();
            if (warning != null)
                System.out.println(warning.getMessage());
        } catch (Exception e) {
            processSQLError(e);
        }
        if (updateCount != 1)
             System.out.println("Tuple not inserted correctly.");
    }
}

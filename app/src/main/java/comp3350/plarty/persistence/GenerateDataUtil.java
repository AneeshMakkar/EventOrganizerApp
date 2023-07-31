package comp3350.plarty.persistence;

import org.joda.time.DateTime;

import java.util.HashSet;

import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.SingleEvent;


public class GenerateDataUtil {
    private final int[] eventHours;
    private final int[] eventLength;

    private final String[] firstNames;
    private final String[] lastNames;

    private final String[] event_prefix;
    private final String[] event_suffix;

    private final String[] locations;

    /**
     * Generate Data for DataStub
     * 35 Unique Users
     * 144 Events
     * 11 locations
     */
    public GenerateDataUtil(){
        eventHours = new int[]{8, 10, 11, 12, 13, 14, 15, 16, 18}; // 8 events generated per day
        eventLength = new int[]{1, 2, 3, 4}; // max time for an event is 19, or 7

        firstNames = new String[]{"Nadja", "Colin", "Guillermo", "Nandor", "Laszlo", "Troy", "Señor Ben", "Steve"}; // 7
        lastNames = new String[]{"Robinson", "de la Cruz", "Cravensworth", "Barnes", "Chang"}; // 5
        // 35 Unique Users

        event_prefix = new String[]{"Surprise", "My",  "Bowling", "Marathon", "Dance", "Art", "Marriage", "Tea", "Gossip", "Disco", "Tennis", "Circus"}; // 12
        event_suffix = new String[]{"Party", "Assembly", "Meetup", "Get Together", "Function", "Reception", "Gathering", "Event", "Bash", "Barbeque", "Soiree", "Banquet"}; //12
        // 144 Events, A Third prefix would scale this a ton, but is likely not needed. Currently ~5 events per user

        locations = new String[]{"Milwaukee", "Detroit, Rock City", "University of Manitoba", "My House", "Behind McDonalds", "Burger King", "Winnipeg", "Manchester", "Albuquerque", "University of Winnipeg", "My Mee Maws house"};
    }

    /**
     * Create 35 Unique users in DataAccess
     */
    private void generateUsers(DataAccess dataAccess){
        dataAccess.createUser("John Braico"); // Main User (Always)
        for(String firstName : firstNames){
            for(String lastName : lastNames){
                dataAccess.createUser(firstName + " " + lastName);
            }
        }
    }

    /**
     * Create Events using data(Users,Events,location and Time) from data Util.
     */
    private void generateEvents(DataAccess dataAccess){
        DateTime startDay = DateTime.now().withTimeAtStartOfDay().withMonthOfYear(9).withDayOfMonth(1);
        int userCount = firstNames.length * lastNames.length + 1;
        int curUser = 0, curLocation = 0, lengthNum = 0, eventHoursNum = 0;
        for(String prefix : event_prefix){
            for(String suffix : event_suffix){
                startDay = startDay.withHourOfDay(eventHours[eventHoursNum]);
                Event event = new SingleEvent(prefix + " " + suffix, dataAccess.assignEventId(), dataAccess.getUser(curUser), locations[curLocation], startDay, startDay.plusHours(eventLength[lengthNum]));
                dataAccess.createEvent(event);

                curUser = (curUser + 1)%userCount;
                curLocation = (curLocation+1)%locations.length;
                lengthNum = (lengthNum+1)%eventLength.length;
                eventHoursNum = (eventHoursNum+1)%eventHours.length;
                if(lengthNum == 0){
                    startDay = startDay.plusDays(1);
                }
            }
        }
    }

    private InviteResponse castIntToResponse(int x) {
        switch(x%3) {
            case 0:
                return InviteResponse.ACCEPTED;
            case 1:
                return InviteResponse.MAYBE;
            case 2:
                return InviteResponse.DECLINED;
        }
        return InviteResponse.NO_RESPONSE;
    }

    /**
     * Create and Update invited Users for a given Event.
     */
    private void generateInvites(DataAccess dataAccess){
        int totalEvents = dataAccess.assignEventId();
        int invited_per_event = 2;
        int totalUsers = firstNames.length * lastNames.length + 1;
        int curUser = 0, responseNum = 0;
        for(int i = 0; i<totalEvents; i++){
            if(dataAccess.getEvent(i).getOrganiser().getId() != 0) {
                for (int j = 0; j<invited_per_event; j++) {
                    if(dataAccess.getEvent(i).getOrganiserId() != curUser){
                        dataAccess.createInvite(i, curUser);
                        if(curUser != 0){
                            dataAccess.updateInvite(i, curUser, castIntToResponse(responseNum++));
                        }
                    }
                    curUser = (curUser + 1) % totalUsers;
                }
            }
        }
    }

    /**
     * Create Recurring Events
     */
    private void generateRecurrentEvents(DataAccess dataAccess){
        int totalUsers = firstNames.length * lastNames.length + 1;
        DateTime startTime = DateTime.now().withTimeAtStartOfDay().withHourOfDay(23);
        DateTime endTime = DateTime.now().withTimeAtStartOfDay().plusDays(1).withHourOfDay(7);
        HashSet<Integer> daysOfWeek = new HashSet<>();
        for(int i = 1; i<=7; i++)
            daysOfWeek.add(i);
        for(int i = 1; i<totalUsers; i++){
            RecurringEventGenerator event = new RecurringEventGenerator("Sleep Time", dataAccess.assignEventId(), dataAccess.getUser(i), "My House", startTime, endTime, daysOfWeek);
            dataAccess.createEvent(event);
            dataAccess.createInactive(event.getId(), i);
        }
    }

    /**
     * Populate the DataBase with Users, Events, Invites and Recurring Events
     */
    public void populateData(DataAccess dataAccess){
        generateUsers(dataAccess);
        generateEvents(dataAccess);
        generateInvites(dataAccess);
        generateRecurrentEvents(dataAccess);
    }
}

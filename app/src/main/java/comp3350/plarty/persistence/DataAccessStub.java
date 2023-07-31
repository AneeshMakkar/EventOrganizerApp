package comp3350.plarty.persistence;
import java.util.ArrayList;
import java.util.HashMap;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;


public class DataAccessStub implements DataAccess {
    private int userCount;
    private HashMap<Integer, Event> events;
    private HashMap<Integer, User> users;
    private HashMap<User, HashMap<Event, InviteResponse>> invitations;
    private HashMap<User, Event> inactive;

    private int mainUserId;
    public DataAccessStub() {  }

    public void open(String dbName) {
        mainUserId = 0;
        if(events == null)
            initialize();
        System.out.println("Opened Data Access Stub");
    }

    private void initialize(){
        userCount = 0;
        events = new HashMap<>();
        users = new HashMap<>();
        invitations = new HashMap<>();
        inactive = new HashMap<>();
        GenerateDataUtil generateDataUtil = new GenerateDataUtil();
        generateDataUtil.populateData(this);
    }

    public void close() {
        System.out.println("Closed Data Access Stub");
    }

    public User getMainUser() {
        return getUser(mainUserId);
    }

    public boolean createEvent(Event event) {
        events.put(event.getId(), event);
        return true;
    }

    public User createUser(String name) {
        User u = new User(name, userCount);
        users.put(userCount++, u);
        return u;
    }

    public boolean deleteEvent(int id) {
        return events.remove(id) != null;
    }

    public Event getEvent(int id) {
        return events.get(id);
    }

    /**
     * get all the event owned by a given user
     */
    public ArrayList<Event> getOwnedEvents(int id) {
        ArrayList<Event> ownedEvents = new ArrayList<>();
        for (Event event : events.values()) {
            if (id == event.getOrganiserId()) {
                ownedEvents.add(event);
            }
        }
        return ownedEvents;
    }

    /**
     * get the Hashmap of events and Users invite response for the event
     */
    public HashMap<Event, InviteResponse> getInvitations(int userId) {
        HashMap<Event, InviteResponse> userInvitations = new HashMap<>();
        if (userId >= 0 && userId < users.size()) {
            userInvitations = invitations.get(getUser(userId));
            if(userInvitations == null)
                userInvitations = new HashMap<>();
        }
        return userInvitations;
    }

    /**
     * get all invited Users
     */
    public HashMap<User, InviteResponse> getInvitedUsers(SingleEvent event) {
        HashMap<User, InviteResponse> eventInvitations = new HashMap<>();
        if (event.getId() >= 0 && event.getId() < events.size()) {
            for(User user : invitations.keySet()){
                HashMap<Event, InviteResponse> eventInvite = invitations.get(user) ;
                if(eventInvite != null) {
                    for(Event userEvents : eventInvite.keySet()){
                        if(userEvents.equals(event)){
                            eventInvitations.put(user, eventInvite.get(userEvents));
                        }
                    }
                }
            }
        }
        return eventInvitations;
    }

    public int assignEventId() {
        return events.size();
    }

    public User getUser(int id) {
        return users.get(id);
    }

    /**
     * get all the Users
     */
    public ArrayList<User> getAllOtherUsers(int id) {
        ArrayList<User> result = new ArrayList<>();
        for(int i = 0; i < userCount; i++) {
            result.add(users.get(i));
        }
        return result;
    }

    /**
     * Update the given event
     */
    public boolean updateEvent(Event event) {
        boolean success = false;
        if(events.containsKey(event.getId())) {
            events.put(event.getId(), event);
            success=true;
        }
        return success;
    }

    /**
     * update the User
     */
    public boolean updateUser(User user) {
        boolean success = false;

        if(users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            success=true;
        }

        return success;
    }

    /**
     * Invite the user for a given event
     */
    public boolean createInvite(int eventId, int userId) {
        boolean invitationCreated = false;
        Event event = events.get(eventId);
        User user = users.get(userId);

        if (user != null && event instanceof SingleEvent) {
            HashMap <Event, InviteResponse> eventResponse = invitations.get(user);
            if(eventResponse == null){
                eventResponse = new HashMap<>();
                invitations.put(user, eventResponse);
            }
            eventResponse.put(event, InviteResponse.NO_RESPONSE);
            invitationCreated = true;
        }
        return invitationCreated;
    }

    /**
     * Update the invite response for a given event
     */
    public boolean updateInvite(int eventId, int userId, InviteResponse response) {
        boolean inviteUpdated = false;
        Event event = getEvent(eventId);
        User user = getUser(userId);

        if (user != null && event instanceof SingleEvent) {
            HashMap <Event, InviteResponse> eventResponse = invitations.get(user);
            if(eventResponse != null && eventResponse.containsKey(event)) {
                eventResponse.put(event, response);
                inviteUpdated = true;
            }
        }
        return inviteUpdated;
    }

    /**
     * Get the Invite Response of the User for an Event
     */
    public InviteResponse getInvite(int eventId, int userId) {
        InviteResponse result = null;
        User user = users.get(userId);
        Event event = events.get(eventId);
        if(event instanceof SingleEvent) {
            HashMap<Event, InviteResponse> intermediate = invitations.get(user);
            if (intermediate != null)
                result = intermediate.get(event);
        }
        return result;
    }

    /**
     * Delete Invite
     */
    public boolean deleteInvite(int eventId, int userId){
        boolean inviteDeleted = false;
        if (eventId >= 0 && eventId < events.size() && userId >= 0 && userId < users.size()) {
            invitations.remove(users.get(userId));
            inviteDeleted = true;
        }
        return inviteDeleted;
    }

    /**
     * Create Inactive Events
     */
    public boolean createInactive(int eventId, int userId) {
        boolean result = false;
        Event event = getEvent(eventId);
        User user = getUser(userId);

        if(event instanceof RecurringEventGenerator) {
            inactive.put(user, event);
            result = true;
        }

        return result;
    }

    /**
     * get Inactive Events
     */
    public Event getInactive(int userId) {
        User user = getUser(userId);
        Event event = null;
        if(inactive.containsKey(user))
            event = inactive.get(user);

        return event;
    }
}

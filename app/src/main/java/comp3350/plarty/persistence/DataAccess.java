package comp3350.plarty.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;

/**
 * The persistence layer of Plarty. These functions are used by both the
 * HSQLDB database and the test stub database.
 * Most functions are self-explanatory, but others have niche use cases that
 * are documented here.
 * The DataAccessObject implementation has exclusive functions not included
 * in the interface.
 */
public interface DataAccess {

    void open(String dbName);

    void close();

    boolean createEvent(Event event);

    int assignEventId();

    Event getEvent(int id);

    ArrayList<Event> getOwnedEvents(int user);

    boolean updateEvent(Event event);

    boolean deleteEvent(int id);

    User createUser(String name);

    User getUser(int id);

    /**
     * Retrieves the "main user," the default logged-in user from which all
     * emulator actions come.
     *
     * @return      an object with the main user's information
     */
    User getMainUser();

    /**
     * Returns a list of Plarty users other than the specified ID. Used when
     * sending invitations.
     *
     * @param id    the ID of the user requesting the other users
     * @return      a list of all users with different IDs than the given
     */
    ArrayList<User> getAllOtherUsers(int id);

    boolean updateUser(User user);

    boolean createInvite(int eventId, int userId);

    InviteResponse getInvite(int eventId, int userId);

    boolean updateInvite(int eventId, int userId, InviteResponse response);

    boolean deleteInvite(int eventId, int userId);

    /**
     * Retrieves all the events a given user has been invited to as well as
     * the user's response.
     *
     * @param user      the user to query for invitations
     * @return          a HashMap mapping Events to the user's respective response
     */
    HashMap<Event, InviteResponse> getInvitations(int user);

    /**
     * Retrieves all the responses to a given event.
     *
     * @param event     the event to query for invitations
     * @return          a HashMap mapping Users to their respective responses to
     *                  this event's invitation
     */
    HashMap<User, InviteResponse> getInvitedUsers(SingleEvent event);

    boolean createInactive(int eventId, int userId);

    Event getInactive(int userId);
}
package comp3350.plarty.business;

import org.joda.time.DateTime;
import comp3350.plarty.application.Services;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.User;
import comp3350.plarty.persistence.DataAccess;

public abstract class AccessEvents {
    protected final DataAccess dataAccess;
    protected final AccessUsers accessUsers;

    public AccessEvents() {
        dataAccess =  Services.getDataAccess();
        accessUsers = new AccessUsers();
    }

    public Event getEvent(int id) {
        return dataAccess.getEvent(id);
    }

    /**
     * Removes all values, scrubs invite list, removes event from data stub
     *
     * @param eventId   the ID of the event to delete
     * @return          true if the delete operation was successful
     *                  false if the delete was unsuccessful
     */
    public boolean deleteEvent(int eventId) {
        return dataAccess.deleteEvent(eventId);
    }

    /**
     * Checks if the given information can create a valid event
     *
     * @param name          the name of the event
     * @param organiser     the event's organiser
     * @param location      the location of the event
     * @param startDate     the timestamp of the event's beginning as a DateTime
     * @param endDate       the DateTime timestamp of the event's end
     * @return              true if the event with this information can be created
     *                      false if the event information is invalid
     */
    public boolean validEvent(String name, User organiser, String location, DateTime startDate, DateTime endDate) {
        return (name != null && organiser != null && location != null &&
                new TimeManagement().validInterval(startDate, endDate) && !name.trim().equals("") && !location.equals(""));
    }

    /**
     * Queries the database to see if a user has organized a given event
     *
     * @param event     the event to check
     * @param user      the user to check
     * @return          true if the user is the event's organizer
     *                  false if not
     */
    public boolean isUserAnOrganizer(Event event, User user) {
        return (event != null && user != null && user.getId() == event.getOrganiser().getId());
    }

    /**
     * Queries the database for existing events and returns an available ID
     *
     * @return      the ID returned by the database
     */
    protected int assignEventId() { return dataAccess.assignEventId(); }
}

package comp3350.plarty.business;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import comp3350.plarty.application.Services;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.User;
import comp3350.plarty.objects.ObjectValidator;
import comp3350.plarty.persistence.DataAccess;

/**
 * This class provides access functions for Users.
 * This works in tandem with the dataAccess.
 * Provided functionality includes User creation, User retrieval,
 * receiving invitations, responding to invitations, adding/removing from
 * a user schedule.
 */
public class AccessUsers {
    private final DataAccess dataAccess;

    public AccessUsers() {
        dataAccess = Services.getDataAccess();
    }

    public User getMainUser() { // returns current "Logged in" user
        return dataAccess.getMainUser();
    }

    /**
     * Given an ID, retrieves that user from the database.
     * 
     * @param id    the ID of the user to retrieve
     * @return      the user with the given id, if found in the database;
     *              null if the user does not exist
     */
    public User getUser(int id) {
        User user = dataAccess.getUser(id);
        try {
            ObjectValidator.nullCheck(user, "AccessUsers - getUser");
        } catch (IllegalArgumentException IAE) {
            System.err.println("receiveInvite - IllegalArgumentException: " + IAE.getMessage());
        }
        return user;
    }

    public User createUser(String name) {
        return dataAccess.createUser(name);
    }

    /**
     * Deletes the event from the user's invitation list.
     * 
     * @param event     the user to uninvite from the given event
     * @param user      the event to uninvite the given user from
     * @return          true if the invite could be withdrawn
     *                  false if the parameters are invalid, or if the database
     *                  could not perform the delete
     */
    public boolean withdrawInvite(User user, Event event) {
        boolean invitationWithdrawn = false;
        try {
            ObjectValidator.nullCheck(event, "Event");
            ObjectValidator.nullCheck(user, "User");

            if (dataAccess.getInvite(event.getId(), user.getId()) != null) {
                if (dataAccess.deleteInvite(event.getId(), user.getId()))
                    invitationWithdrawn = true;
            }
        } catch (IllegalArgumentException IAE) {
            System.err.println("withdrawInvite - IllegalArgumentException: " + IAE.getMessage());
        }

        return invitationWithdrawn;
    }

    /**
     * Changes the user's response to a given event.
     * 
     * @param user          the user who is responding to the invitation
     * @param event         the event to which the user was invited
     * @param response      whether the user accepts, declines, or answers "maybe"
     * @return              true if the user's response is recorded in the database
     *                      false if the parameters are invalid, the user cannot
     *                      accept, or if the database cannot save the response
     */
    public boolean respondToInvite(User user, Event event, InviteResponse response) {
        boolean invitationResponded = false;
        try {
            ObjectValidator.nullCheck(event, "Event");
            ObjectValidator.nullCheck(user, "User");

            if(response != InviteResponse.ACCEPTED || userIsAvailable(user, event.getStart(), event.getEnd())) {
                if (dataAccess.getInvite(event.getId(), user.getId()) != null) {
                    invitationResponded = dataAccess.updateInvite(event.getId(), user.getId(), response);
                }
            }
        } catch (IllegalArgumentException IAE) {
            System.err.println("respondToInvite - IllegalArgumentException: " + IAE.getMessage());
        }
        return invitationResponded;
    }

    /**
     * Retrieves a list of all events a User is attending in a given timeframe.
     * 
     * @param user          the user to query for events
     * @param startTime     the beginning of the timeframe
     * @param endTime       the end of the timeframe
     * @return              a list of a user's events that fall within the
     *                      timeframe, including events that start before it
     *                      but end within it, or start within it but end after it
     */
    public ArrayList<Event> getSchedule(User user, DateTime startTime, DateTime endTime) {
        ArrayList<Event> result = new ArrayList<>();
        ArrayList<Event> userEvents = getAttendingEvents(user);
        for (Event event : userEvents) {
            event.generate(startTime, endTime, result);
        }
        return result;
    }

    /**
     * Retrieves the entire list of a user's events, including ones they've
     * created and ones they've accepted invitations to.
     *
     * @param user      the user to query for events
     * @return          a list of events the user has either created or accepted
     *                  invitations to
     */
    public ArrayList<Event> getAttendingEvents(User user){
        ArrayList<Event> result = new ArrayList<>();

        try {
            ObjectValidator.nullCheck(user, "User");

            HashMap<Event, InviteResponse> invitations = dataAccess.getInvitations(user.getId());
            ArrayList<Event> ownedSet = getOwnedEvents(user);
            result.addAll(ownedSet);
            for(Event event : invitations.keySet()){
                if(invitations.get(event) == InviteResponse.ACCEPTED){
                    result.add(event);
                }
            }
        } catch(IllegalArgumentException IAE) {
            System.err.println("getAttendingEvents - IllegalArgumentException: " + IAE.getMessage());
        }

        return result;
    }

    /**
     * Retrieves a list of events organized by a given user.
     *
     * @param user      the user to query for organized events
     * @return          a list of events with the user as their organizer
     */
    public ArrayList<Event> getOwnedEvents(User user){
        ArrayList<Event> ownedEvents = new ArrayList<>();
        try {
            ObjectValidator.nullCheck(user, "User");

            ownedEvents = dataAccess.getOwnedEvents(user.getId());
        } catch(IllegalArgumentException IAE) {
            System.err.println("getOwnedEvents - IllegalArgumentException: " + IAE.getMessage());
        }
        return ownedEvents;
    }

    /**
     * Retrieves a user's events, excluding their "inactive hours" event.
     * This is used for setting new active hours and making sure there aren't
     * any "false positive" collisions with the old inactive event.
     *
     * @param user      the user to query for events
     * @return          a list of events the user is attending, sorted by start
     *                  time, with their inactive hours event removed
     */
    public ArrayList<Event> getNonInactiveEvents(User user) {
        ArrayList<Event> schedule = getAttendingEvents(user);
        Event inactive = getInactive(user);
        Event toRemove = null;
        for(Event event : schedule) {
            if(event.equals(inactive)) {
                toRemove = event;
            }
        }
        if(toRemove != null) {
            schedule.remove(toRemove);
        }

        Collections.sort(schedule, (e1, e2) -> Long.compare(e1.getStart().getMillis(), e2.getStart().getMillis()));
        return schedule;
    }

    public HashMap<Event, InviteResponse> getInvitationResponses(User user) {
        return dataAccess.getInvitations(user.getId());
    }

    /**
     * Finds what a user has responded to their invitation to a given event.
     *
     * @param user      the user to query for a response
     * @param event     the event the user may have responded to
     * @return          the user's recorded response; this will be NO_RESPONSE
     *                  if the user hasn't responded, or if they have no
     *                  recorded invitation to this event
     */
    public InviteResponse getResponseToEvent(User user, Event event) {
        InviteResponse response = InviteResponse.NO_RESPONSE;
        HashMap<Event, InviteResponse> responses = getInvitationResponses(user);

        for(Event respondedEvent : responses.keySet()) {
            if(respondedEvent.equals(event)) {
                response = responses.get(respondedEvent);
            }
        }

        return response;
    }

    /**
     * Retrieves a list of events a user has been invited to, regardless of
     * invite response or availability.
     *
     * @param user      the user to query for invitations
     * @return          a list of events the user has been invited to, sorted
     *                  by start time
     */
    public ArrayList<Event> getInvitedEvents(User user) {
        ArrayList<Event> invited = new ArrayList<>(getInvitationResponses(user).keySet());
        Collections.sort(invited, (e1, e2) -> Long.compare(e1.getStart().getMillis(), e2.getStart().getMillis()));
        return invited;
    }

    /**
     * Checks if the user is available at a certain timeframe.
     *
     * @param user          the user to query for availability
     * @param startTime     when the new timeframe begins
     * @param endTime       when the new timeframe ends
     * @return              true if the user has no events overlapping the interval
     *                      false if not
     */
    public boolean userIsAvailable(User user, DateTime startTime, DateTime endTime) {
        return getConflictingEvents(user, startTime, endTime).size() == 0;
    }

    /**
     * Given a timeframe, retrieves all of the events a user is attending that
     * overlap with it.
     *
     * @param user          the user to query for availability
     * @param startTime     the beginning of the timeframe
     * @param endTime       the end of the timeframe
     * @return              a list of events that overlap with the given timeframe
     */
    public ArrayList<Event> getConflictingEvents(User user, DateTime startTime, DateTime endTime) {
        ArrayList<Event> conflicts = new ArrayList<>();
        try {
            ObjectValidator.nullCheck(startTime, "Starting date and time");
            ObjectValidator.nullCheck(endTime, "Ending date and time");

            Interval givenTime = new Interval(startTime, endTime);
            for (Event uEvent : getSchedule(user, startTime.minusDays(7), endTime.plusDays(7))) {
                if (givenTime.overlaps(uEvent.getInterval())) {
                    conflicts.add(uEvent);
                }
            }
        } catch (IllegalArgumentException IAE) {
            System.err.println("getConflictingEvents - IllegalArgumentException: " + IAE.getMessage());
        }
        return conflicts;
    }

    /**
     * Compares a timeframe against a list of events, and sees if any of them
     * overlap with the interval. If there are any overlaps, a
     * RecurringEventGenerator cannot be created with the given timeframe.
     *
     * @param start     the beginning of the timeframe
     * @param end       the end of the timeframe
     * @param schedule  the events to check for overlaps
     * @return          true if no events in the schedule overlap with the
     *                  interval created by the start and end times
     *                  false if there are any conflicts with the timeframe
     */
    public boolean canCreateRecurring(DateTime start, DateTime end, ArrayList<Event> schedule) {
        boolean success = false;
        long recurrentPeriod = end.getMillis() - start.getMillis();
        if(!start.equals(end)) {
            //assume we can add create a generator, until we find a collision (if at all)
            success = true;
            for(Event event : schedule) {
                //to find a collision, we create the child interval that would occur on the same day as the scheduled event
                //if there's an overlap, we cannot change the active hours
                DateTime childStart = event.getStart().withMillisOfDay(start.getMillisOfDay());
                DateTime childEnd = childStart.plusMillis((int)recurrentPeriod);
                DateTime childStart2 = childStart.minusDays(1);
                DateTime childEnd2 = childEnd.minusDays(1);
                DateTime childStart3 = childStart.plusDays(1);
                DateTime childEnd3 = childEnd.plusDays(1);
                Interval child = new Interval(childStart, childEnd);
                Interval child2 = new Interval(childStart2, childEnd2);
                Interval child3 = new Interval(childStart3, childEnd3);
                if(child.overlaps(event.getInterval()) || child2.overlaps(event.getInterval()) || child3.overlaps(event.getInterval())) {
                    success = false;
                    break;
                }
            }
        }
        return success;
    }

    /**
     * Checks if a user has been invited to an event.
     *
     * @param event     the event to look for in invitations
     * @param user      the user to query for invitations
     * @return          true if an invitation to this event exists
     *                  false if the user was not invited to this event
     */
    public boolean isUserInvited(Event event, User user) {
        boolean isInvited = false;
        try {
            ObjectValidator.nullCheck(user, "User");

            HashMap<Event, InviteResponse> userInvites = dataAccess.getInvitations(user.getId());
            isInvited = userInvites.containsKey(event);
        } catch (IllegalArgumentException IAE) {
            System.err.println("isUserInvited - IllegalArgumentException: " + IAE.getMessage());
        }
        return isInvited;
    }

    public ArrayList<User> getAllOtherUsers(int id) {
        return dataAccess.getAllOtherUsers(id);
    }

    /**
     * Queries the database for a user's corresponding inactive event.
     *
     * @param user      the user to find inactive hours for
     * @return          the inactive hours event for the user, or null
     *                  if one hasn't been created
     */
    public Event getInactive(User user) {
        Event inactive = null;
        try {
            ObjectValidator.nullCheck(user, "User");

            inactive = dataAccess.getInactive(user.getId());
        } catch (IllegalArgumentException IAE) {
            System.err.println("isUserInvited - IllegalArgumentException: " + IAE.getMessage());
        }
        return inactive;
    }

    /**
     * getters for Inactive time for User
     * getInactiveStartHour- get Inactive Start time in hours
     * getInactiveStartMinute- get Inactive Start time in minutes
     * getInactiveEndHour- get Inactive end time in hours
     * getInactiveEndMinute- get Inactive end time in Minutes
     */
    public int getInactiveStartHour(User user) {
        return getInactive(user).getStart().getHourOfDay();
    }

    public int getInactiveStartMinute(User user) {
        return getInactive(user).getStart().getMinuteOfHour();
    }

    public int getInactiveEndHour(User user) {
        return getInactive(user).getEnd().getHourOfDay();
    }

    public int getInactiveEndMinute(User user) {
        return getInactive(user).getEnd().getMinuteOfHour();
    }
}
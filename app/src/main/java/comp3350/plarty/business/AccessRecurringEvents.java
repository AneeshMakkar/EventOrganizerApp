package comp3350.plarty.business;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.HashSet;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.User;
import comp3350.plarty.objects.ObjectValidator;

/**
 * This class provides access functions for Recurring Events.
 * This works in tandem with the dataAccess.
 * Provided functionality includes generating child events
 * and altering parent event generation.
 */
public class AccessRecurringEvents extends AccessEvents {
    private static final HashSet<Integer> allDays = new HashSet<Integer>(){{
        add(1);
        add(2);
        add(3);
        add(4);
        add(5);
        add(6);
        add(7);
    }};

    /**
     * Given event metadata, creates a recurring event generator for the user
     *
     * @param name          the name of the recurring event
     * @param organiser     the user who creates the event
     * @param location      the name of the event's location
     * @param start         when the event starts as a DateTime;
     *                      only the time is used
     * @param end           when the event ends as a DateTime (time used only)
     * @param daysOfWeek    the weekdays on which the event recurs,
     *                      represented as integer constants
     *                      (1 = monday, 2 = tuesday, etc)
     * @param schedule      a list of events to compare against for collisions
     *                      depending on the context, we may only want to consider
     *                      certain events (see setActiveHours function for example)
     * @return              the created RecurringEventGenerator, or null if
     *                      creation failed
     */
    public Event createRecurringEvent(String name, User organiser, String location, DateTime start, DateTime end, HashSet<Integer> daysOfWeek, ArrayList<Event> schedule) {
        Event event = null;
        if(validEvent(name, organiser, location, start, end)
                && canRecur(start, end)
                && accessUsers.canCreateRecurring(start, end, schedule)) {
            event = new RecurringEventGenerator(name, assignEventId(), organiser, location, start, end, daysOfWeek);
            dataAccess.createEvent(event);
            System.out.println(event);
        }
        return event;
    }

    /**
     * Creates a RecurringEventGenerator representing a user's "Inactive
     * hours," the opposite of the "ACTIVE hours" they specify in the
     * presentation layer
     *
     * @param user          the user to set inactive hours for
     * @param startHour     the hour the user's ACTIVE time starts
     * @param startMinute   the minute of the active time's start
     * @param endHour       the hour the user's active time ends
     * @param endMinute     the minute of the active time's end
     * @return              true if the Inactive event generator was created
     *                      false if the inactive event conflicts with an
     *                      existing event
     */
    public boolean setActiveHours(User user, int startHour, int startMinute, int endHour, int endMinute) {
        boolean result = false;

        try {
            ObjectValidator.nullCheck(user, "User");

            //"active hours" is actually the opposite of the "inactive" event- it's a little confusing!
            DateTime end = DateTime.now().withTime(startHour, startMinute, 0, 0);
            DateTime start = DateTime.now().withTime(endHour, endMinute, 0, 0);
            if(end.isBefore(start)) {
                start = start.minusDays(1);
            }
            Event inactive = createRecurringEvent("Inactive", user, "None", start, end, allDays, accessUsers.getNonInactiveEvents(user));
            if(inactive != null) {
                result = true;
                dataAccess.createInactive(inactive.getId(), user.getId());
            }
        } catch(IllegalArgumentException IAE) {
            System.err.println("setActiveHours - IllegalArgumentException: " + IAE.getMessage());
        }
        return result;
    }

    /**
     * Checks if the interval is less than 24 hours so it can recur
     * every day if necessary
     *
     * @param start     the start of an interval
     * @param end       the end of an interval
     * @return          true if the interval is less than 24 hours
     *                  false if not
     */
    private boolean canRecur(DateTime start, DateTime end) {
        boolean result = false;
        try {
            ObjectValidator.nullCheck(start, "Start time");
            ObjectValidator.nullCheck(end, "End time");

            result = end.getMillis() - start.getMillis() <= DateTimeConstants.MILLIS_PER_DAY;
        } catch(IllegalArgumentException IAE) {
            System.err.println("canRecur - IllegalArgumentException: " + IAE.getMessage());
        }
        return result;
    }

    /**
     * Edits a single day's instance of a RecurringEvent without changing the entire
     * generator
     *
     * @param event     the Generator parent of the instance
     * @param day       the day of the specific instance to edit
     * @param interval  the new interval for the given day's RecurringEvent instance
     */
    public void createAlteration(RecurringEventGenerator event, DateTime day, Interval interval){
        try {
            ObjectValidator.nullCheck(event, "Recurring event generator");
            ObjectValidator.nullCheck(day, "Day of alteration");
            ObjectValidator.nullCheck(interval, "Altered interval");

            event.createAlteration(day.withTimeAtStartOfDay(), interval);
        } catch (IllegalArgumentException IAE) {
            System.err.println("createAlteration - IllegalArgumentException: " + IAE.getMessage());
        }
    }

    /**
     * Retrieves the event alteration for a specific date
     *
     * @param event     the Generator parent to check for alterations
     * @param date      the date to look for an alteration on
     * @return          the altered interval of the RecurringEvent if it exists;
     *                  null if there is no alteration on the given date
     */
    public Interval getAlteration(RecurringEventGenerator event, DateTime date) {
        Interval newInterval = null;
        try {
            ObjectValidator.nullCheck(event, "Recurring event generator");

            newInterval = event.getAlteration(date);
        } catch(IllegalArgumentException IAE) {
            System.err.println("getAlteration - IllegalArgumentException: " + IAE.getMessage());
        }
        return newInterval;
    }

    /**
     * Removes all alterations for a certain RecurringEventGenerator.
     *
     * @param event     the Generator to reset
     */
    public void clearAlterations(RecurringEventGenerator event) {
        try {
            ObjectValidator.nullCheck(event, "Recurring event generator");

            event.clearAlterations();
        } catch(IllegalArgumentException IAE) {
            System.err.println("clearAlterations - IllegalArgumentException: " + IAE.getMessage());
        }
    }

    /**
     * Deletes a RecurringEvent instance on a specific day.
     *
     * @param event     the Generator parent of the instance to delete
     * @param day       the date of the instance to delete
     */
    public void deleteDay(RecurringEventGenerator event, DateTime day){
        try {
            ObjectValidator.nullCheck(event, "Recurring event generator");
            ObjectValidator.nullCheck(day, "Day of alteration");

            event.createAlteration(day.withTimeAtStartOfDay(), null);
        } catch(IllegalArgumentException IAE) {
            System.err.println("deleteDay - IllegalArgumentException: " + IAE.getMessage());
        }
    }

    /**
     * Edits the beginning of the time interval for all unaltered children
     */
    public void setRecurringStartTime(RecurringEventGenerator event, DateTime startTime) {
        try {
            ObjectValidator.nullCheck(event, "Recurring event generator");

            event.setStart(startTime);
        } catch(IllegalArgumentException IAE) {
            System.err.println("setRecurringStartTime - IllegalArgumentException: " + IAE.getMessage());
        }
    }

	/**
     * Edits the end of the time interval for all unaltered children
     */
    public void setRecurringEndTime(RecurringEventGenerator event, DateTime endTime) {
        try {
            ObjectValidator.nullCheck(event, "Recurring event generator");

            event.setEnd(endTime);
        } catch(IllegalArgumentException IAE) {
            System.err.println("setRecurringEndTime - IllegalArgumentException: " + IAE.getMessage());
        }
    }
}

package comp3350.plarty.business;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;

import java.util.ArrayList;

import comp3350.plarty.objects.RecurringEvent;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.ObjectValidator;

/**
 * This class provides access functions for Generating Recurrent Events.
 */
public class GenerateRecurringEvents {

    /**
     * Takes a start and end time from an interval and applies it to a given day.
     *
     * @param startDay      the day on which we create a new interval
     * @param interval      the times we will use for our new interval
     * @return              the interval generated on the given day
     */
    public Interval getIntervalFromDefault(DateTime startDay, Interval interval) {
        Interval defaultInterval = null;

        try {
            ObjectValidator.nullCheck(startDay, "startDay");
            ObjectValidator.nullCheck(interval, "interval");

            DateTime startTime = interval.getStart();
            DateTime endTime = interval.getEnd();

            int minuteLength = (endTime.getMinuteOfDay() - startTime.getMinuteOfDay() + DateTimeConstants.MINUTES_PER_DAY) % DateTimeConstants.MINUTES_PER_DAY;
            DateTime intervalStart = startDay.plusMinutes(startTime.getMinuteOfDay());
            DateTime intervalEnd = intervalStart.plusMinutes(minuteLength);

            defaultInterval = new Interval(intervalStart, intervalEnd);
        } catch (IllegalArgumentException IAE) {
            System.err.println("getIntervalFromDefault - IllegalArgumentException: " + IAE.getMessage());
        }

        return defaultInterval;
    }

    /**
     * Generates recurring events in a given date range.
     *
     * @param event         the event generator we create RecurringEvent objects from
     * @param startDay      the beginning of the date range to generate during
     * @param endDay        the end of the date range
     * @return              a list of RecurringEvent objects with the generator's
     *                      metadata that fall within the date range
     */
    public ArrayList<RecurringEvent> generateEvents(RecurringEventGenerator event, DateTime startDay, DateTime endDay) {
        ArrayList<RecurringEvent> eventGenerated = new ArrayList<>();
        try {
            ObjectValidator.nullCheck(event, "event");
            ObjectValidator.nullCheck(startDay, "startDay");
            ObjectValidator.nullCheck(endDay, "endDay");

            startDay = startDay.withTimeAtStartOfDay();
            endDay = endDay.withTimeAtStartOfDay();

            for (; startDay.isBefore(endDay) || startDay.isEqual(endDay); startDay = startDay.plusDays(1)) {
                if (!event.getDaysOfWeek().contains(startDay.getDayOfWeek()))
                    continue;
                Interval to_add = getIntervalFromDefault(startDay, event.getInterval());
                if (event.getAlterations().containsKey(startDay)) {
                    Interval contained = event.getAlteration(startDay);
                    if (contained == null)
                        continue;
                    to_add = getIntervalFromDefault(startDay, contained);
                }
                RecurringEvent child = new RecurringEvent(event.getName(), event.getId(), event.getOrganiser(), event.getLocation(), to_add.getStart(), to_add.getEnd(), event);
                eventGenerated.add(child);
            }
        } catch (IllegalArgumentException IAE) {
            System.err.println("generateEvents - IllegalArgumentException: " + IAE.getMessage());
        }
        return eventGenerated;
    }
}

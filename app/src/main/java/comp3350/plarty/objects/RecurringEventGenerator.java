package comp3350.plarty.objects;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import comp3350.plarty.business.GenerateRecurringEvents;

/**
 * RecurringEventGenerator holds information about a recurring event.
 * It is then used to generate RecurringEvent objects, which fill a schedule
 * according to the startTime and endTime of the event.
 */
public class RecurringEventGenerator extends Event {
    private HashSet<Integer> daysOfWeek;
    private final HashMap<DateTime, Interval> alterations;
    private final GenerateRecurringEvents generateRecurringEvents;

    public RecurringEventGenerator(String name, int id, User organiser, String location, DateTime startTime, DateTime endTime, HashSet<Integer> daysOfWeek) {
        super(name, id, organiser, location, startTime, endTime);
        validateDaysOfWeek(daysOfWeek);
        this.daysOfWeek = daysOfWeek;
        this.alterations = new HashMap< >();
        generateRecurringEvents = new GenerateRecurringEvents();
    }

    public HashSet<Integer> getDaysOfWeek() { return this.daysOfWeek; }

    public void setDaysOfWeek(HashSet<Integer> newDaysOfWeek) {
        validateDaysOfWeek(newDaysOfWeek);
        this.daysOfWeek = newDaysOfWeek;
    }

    public HashMap<DateTime, Interval> getAlterations() { return alterations; }

    public Interval getAlteration(DateTime key) {
        return this.alterations.get(key);
    }

    public void createAlteration(DateTime date, Interval newInterval) {
        this.alterations.put(date, newInterval);
    }

    public void clearAlterations() {
        this.alterations.clear();
    }

    private void validateDaysOfWeek(HashSet<Integer> daysOfWeek){
        ObjectValidator.nullCheck(daysOfWeek, "Days of week");
        for(int i : daysOfWeek){
            if (i < 1 || i> 7)
                throw new IllegalArgumentException("Days of week must be between 1 and 7 (inclusive)");
        }
    }

    /**
     * Overrides Event::generate() by generating all children that fall within
     * the given interval, if possible.
     *
     * @param start         the beginning of the timeframe in consideration
     * @param end           the end of the timeframe
     * @param eventSet      the set to add itself to
     */
    @Override
    public void generate(DateTime start, DateTime end, ArrayList<Event> eventSet) {
        eventSet.addAll(generateRecurringEvents.generateEvents(this, start, end));
    }
}

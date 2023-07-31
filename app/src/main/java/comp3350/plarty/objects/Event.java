package comp3350.plarty.objects;

import android.support.annotation.NonNull;
import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import comp3350.plarty.business.TimeManagement;

/**
 * Event Class is an abstract class representing event schedules.
 * Concrete subclasses of Events can be created, displayed in a schedule,
 * and users can be invited to them.
 */
public abstract class Event {
    protected final TimeManagement checkDate;
    protected String name;
    protected int id;
    protected User organiser;
    protected String location;
    protected Interval date;

    /**
     * Constructor for Events, includes event name, event id (unique), the User creating the event,
     * the time in which the event starts and the time in which the event ends.
     * @param name          the name of the event
     * @param id            the event's unique ID
     * @param organiser     the user creating the event
     * @param location      the location of the event
     * @param startDate     the date and time the event starts
     * @param endDate       the date and time the event ends
     */
    public Event(String name, int id, User organiser, String location, DateTime startDate, DateTime endDate) {
        checkDate = new TimeManagement();
        ObjectValidator.stringCheck(name, "Event name");
        ObjectValidator.nullCheck(organiser, "Event organiser");
        ObjectValidator.stringCheck(location, "Event location");
        ObjectValidator.nullCheck(startDate, "Event start date");
        ObjectValidator.nullCheck(startDate, "Event end date");
        if(checkDate.validInterval(startDate, endDate)) {
            this.name = name;
            this.id = id;
            this.organiser = organiser;
            this.location = location;
            this.date = new Interval(startDate, endDate);
        } else {
            throw new IllegalArgumentException("Start Date" + startDate +
                    "to" + endDate + "\n must be a valid interval");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        ObjectValidator.stringCheck(name, "Event name");
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public User getOrganiser() {
        return organiser;
    }

    public int getOrganiserId() {
        return organiser.getId();
    }

    public void setOrganiser(User organiser) {
        ObjectValidator.nullCheck(organiser, "Event organiser");
        this.organiser = organiser;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        ObjectValidator.stringCheck(location, "Event location");
        this.location = location;
    }

    public DateTime getStart() {
        return this.date.getStart();
    }

    public void setStart(DateTime startDate) {
        ObjectValidator.nullCheck(startDate, "Event start date");
        if (checkDate.validInterval(startDate, this.getEnd())) {
            this.date = new Interval(startDate.getMillis(), this.date.getEnd().getMillis());
        }
    }

    public DateTime getEnd() {
        return this.date.getEnd();
    }

    public void setEnd(DateTime endDate) {
        ObjectValidator.nullCheck(endDate, "Event end date");
        if (checkDate.validInterval(this.getStart(), endDate)) {
            this.date = new Interval(this.date.getStart().getMillis(), endDate.getMillis());
        }
    }

    public Interval getInterval() {
        return this.date;
    }

    public boolean equals(Object object) {
        return object instanceof Event && this.id == ((Event) object).getId();
    }

    @NonNull
    public String toString() {
        return "\n Event Name: " + name + "\n\tID: " + id + "\n\tOrganiser Name: " + organiser.getName() +
                "\n\tLocation: " + location;
    }

    /**
     * If this event falls within the given timeframe, it adds itself to the
     * provided event set.
     * This is used for creating "schedules," i.e. lists of event objects.
     *
     * @param start         the beginning of the timeframe in consideration
     * @param end           the end of the timeframe
     * @param eventSet      the set to add itself to
     */
    public void generate(DateTime start, DateTime end, ArrayList<Event> eventSet) {
        if(this.getInterval().overlaps(new Interval(start, end))) {
            eventSet.add(this);
        }
    }
}

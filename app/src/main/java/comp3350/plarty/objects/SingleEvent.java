package comp3350.plarty.objects;

import org.joda.time.DateTime;


/**
 * SingleEvent Class represents a one time Event which occurs in a schedule.
 * SingleEvents can be created, displayed in a schedule, and can be created
 * by Users to invite other users.
 */
public class SingleEvent extends Event {

    public SingleEvent(String name, int id, User organiser, String location, DateTime startDate, DateTime endDate) {
        super(name, id, organiser, location, startDate, endDate);
    }

}

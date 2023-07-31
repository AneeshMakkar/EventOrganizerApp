package comp3350.plarty.business;

import org.joda.time.DateTime;
import org.joda.time.Interval;

import java.util.ArrayList;
import comp3350.plarty.objects.User;
import comp3350.plarty.objects.ObjectValidator;

/**
 * The time management class
 * This includes validating date arguments and
 * suggesting times based on user schedules.
 */
public class TimeManagement {
    /**
     * Returns whether an interval can be created from two given DateTime objects.
     *
     * @param startDate     the DateTime to begin the interval at
     * @param endDate       the DateTime to end the interval at
     * @return              true if the arguments can create an interval
     *                      false if the arguments are invalid
     */
    public boolean validInterval(DateTime startDate, DateTime endDate) {
        return (startDate != null && endDate != null && startDate.isBefore(endDate));
    }

    /**
     * Finds a time interval within a given date range that a list of invitees can attend
     *
     * @param invitees      the invitees whose schedules we will consider
     * @param startRange    the beginning of dates we will consider for our result
     * @param endRange      the end of the dates we will consider
     * @param minScheduled  the length in minutes of the event we want to schedule
     * @return              a time interval of length minScheduled when every
     *                      invitee is free; null if no such times are found
     */
    private Interval getSuggestedTime(ArrayList<User> invitees, DateTime startRange, DateTime endRange, long minScheduled) {
        AccessUsers userAccess = new AccessUsers();
        Interval bestTime = null;
        long totalMin = new Interval(startRange, endRange).toDuration().getStandardMinutes();
        long currPeopleInvited = 0;
        Interval currInterval;
        if (minScheduled > 0) {
            for (long i = 0; i < totalMin - minScheduled && currPeopleInvited < invitees.size(); i += minScheduled) {
                currInterval = new Interval(startRange.plusMinutes((int) i), startRange.plusMinutes((int) (i + minScheduled)));
                int numUsersAvailable = 0;

                for (User user : invitees) {
                    if (userAccess.userIsAvailable(user, currInterval.getStart(), currInterval.getEnd())) {
                        numUsersAvailable++;
                    }
                }
                //updating best time
                if (numUsersAvailable > currPeopleInvited) {
                    currPeopleInvited = numUsersAvailable;
                    bestTime = currInterval;
                }
            }
        }

        return bestTime;

    }

    /**
     * Uses getSuggestedTime to create a list of three times that
     * work for all invitees
     *
     * @param invitees          the invitees to consider
     * @param startRange        the start of the range of dates to consider
     * @param endRange          the end of the dates to consider
     * @param minScheduled      the length in minutes of the scheduling event
     * @return                  a list of 3 or fewer time intervals, all of length
     *                          minScheduled, when each invitee is available
     */
    public ArrayList<Interval> getSuggestedTimeList(ArrayList<User> invitees, DateTime startRange, DateTime endRange, long minScheduled) {
        ArrayList<Interval> SuggestedTime = new ArrayList<>();
        try {
            ObjectValidator.nullCheck(invitees, "invitees");
            ObjectValidator.nullCheck(startRange, "startRange");
            ObjectValidator.nullCheck(endRange, "endRange");

            int schedule = (int) minScheduled;
            int counter = 1;
            if (validInterval(startRange, endRange) && minScheduled > 0) {
                while (startRange.plusMinutes(schedule).isBefore(endRange)) {
                    Interval time = getSuggestedTime(invitees, startRange, endRange, minScheduled);
                    startRange = time.getEnd();
                    SuggestedTime.add(time);
                    if (counter++ >= 3)
                        break;
                }
            }
        } catch (IllegalArgumentException IAE) {
            System.err.println("generateEvents - IllegalArgumentException: " + IAE.getMessage());
        }

        return SuggestedTime;
    }
}

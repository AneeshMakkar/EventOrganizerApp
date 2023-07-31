package comp3350.plarty.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;

import comp3350.plarty.business.GenerateRecurringEvents;
import comp3350.plarty.objects.RecurringEvent;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.User;


public class GenerateRecurringEventsTest {

    GenerateRecurringEvents generate;

    @Before
    public void setUp() {
        generate = new GenerateRecurringEvents();
    }

    @Test
    public void testTypicalGetIntervalFromDefault() {
        System.out.println("\n- Testing: GenerateRecurringEvents - TypicalGetIntervalFromDefault");
        DateTime start1 = new DateTime(2022, 12, 1, 10, 30);
        DateTime start2 = new DateTime(2022, 12, 1, 14, 30);

        DateTime intervalStart = new DateTime(2022, 12, 2, 0, 0);
        DateTime intervalEnd = new DateTime(2022, 12, 10, 1, 0);

        Interval range = new Interval(intervalStart, intervalEnd);
        Interval range2 = new Interval(intervalStart, intervalEnd.plusHours(2));

        System.out.println("\n\t- Test getIntervalFromDefault - One hour interval");
        assertEquals(new Interval(start1, start1.plusHours(1)),
                generate.getIntervalFromDefault(start1, range));

        assertEquals(new Interval(start1.minusHours(1), start1),
                generate.getIntervalFromDefault(start1.minusHours(1), range));

        System.out.println("\n\t- Test getIntervalFromDefault - Four hour interval");
        assertEquals(new Interval(start2, start2.plusHours(3)),
                generate.getIntervalFromDefault(start2, range2));

        assertEquals(new Interval(start2.plusHours(4), start2.plusHours(7)),
                generate.getIntervalFromDefault(start2.plusHours(4), range2));

        System.out.println("\n- Completed Testing: GenerateRecurringEvents - TypicalGetIntervalFromDefault");
    }

    @Test
    public void testEdgeCasesGetIntervalFromDefault() {
        System.out.println("\n- Testing: GenerateRecurringEvents - EdgeCasesGetIntervalFromDefault");
        DateTime start1 = new DateTime(2022, 12, 1, 10, 30);
        DateTime start2 = new DateTime(2022, 8, 12, 14, 30);

        DateTime intervalStart = new DateTime(2022, 12, 1, 0, 0);
        DateTime intervalEnd = new DateTime(2022, 12, 8, 1, 0);

        Interval range = new Interval(intervalStart, intervalEnd);
        Interval range2 = new Interval(intervalStart, intervalEnd.plusHours(2));

        System.out.println(generate.getIntervalFromDefault(start1, range));

        System.out.println("\n\t- Test getIntervalFromDefault - 0 min interval");
        assertEquals(new Interval(start1, start1),
                generate.getIntervalFromDefault(start1, new Interval(intervalStart, intervalStart)));

        System.out.println("\n\t- Test getIntervalFromDefault - start date outside the range");
        assertEquals(new Interval(start2, start2.plusHours(3)),
                generate.getIntervalFromDefault(start2, range2));

        System.out.println("\n\t- Test getIntervalFromDefault - A year apart interval");
        assertEquals(new Interval(start2, start2.plusHours(1)),
                generate.getIntervalFromDefault(start2, new Interval(intervalStart, intervalEnd.plusYears(1))));

        System.out.println("\n- Completed Testing: GenerateRecurringEvents - TypicalGetIntervalFromDefault");
    }

    @Test
    public void testTypicalGenerateEvents() {
        System.out.println("\n- Testing: GenerateRecurringEvents - TypicalGenerateEvents");
        DateTime start1 = new DateTime(2022, 11, 7, 10, 30);

        DateTime start = new DateTime(2022, 11, 5, 2, 0);
        DateTime end = new DateTime(2022, 11, 12, 4, 0);

        User user = new User("User for event", 1);
        HashSet<Integer> threeDays = new HashSet<>();

        threeDays.add(DateTimeConstants.MONDAY);
        threeDays.add(DateTimeConstants.WEDNESDAY);
        threeDays.add(DateTimeConstants.FRIDAY);

        RecurringEventGenerator rEvent = new RecurringEventGenerator("Recurring", 1, user,
                "My house", start1, start1.plusHours(2), threeDays);

        System.out.println("\n\t- Test generateEvents - Checking the whole week for events");
        assertEquals(3, generate.generateEvents(rEvent, start, end).size());

        // Monday
        assertEquals(start1, generate.generateEvents(rEvent, start, end).get(0).getStart());
        assertEquals(start1.plusHours(2), generate.generateEvents(rEvent, start, end).get(0).getEnd());

        // Wednesday
        start1 = start1.plusDays(2);
        assertEquals(start1, generate.generateEvents(rEvent, start, end).get(1).getStart());
        assertEquals(start1.plusHours(2), generate.generateEvents(rEvent, start, end).get(1).getEnd());

        // Friday
        start1 = start1.plusDays(2);
        assertEquals(start1, generate.generateEvents(rEvent, start, end).get(2).getStart());
        assertEquals(start1.plusHours(2), generate.generateEvents(rEvent, start, end).get(2).getEnd());

        System.out.println("\n- Completed Testing: GenerateRecurringEvents - TypicalGenerateEvents");
    }

    @Test
    public void testOneDayGenerateEvents() {
        DateTime start1 = new DateTime(2022, 1, 1, 10, 30);

        ArrayList<RecurringEvent> currCheck;

        User user1 = new User("user1", 1);

        HashSet<Integer> oneDay = new HashSet<>();
        oneDay.add(DateTimeConstants.SATURDAY);

        RecurringEventGenerator oneDayEvent = new RecurringEventGenerator("Recurring", 1, user1,
                "My house", start1, start1.plusHours(2), oneDay);

        System.out.println("\n\t- Test createEvent - Once a week event, check full week");
        currCheck = generate.generateEvents(oneDayEvent, oneDayEvent.getStart().minusDays(1),
                oneDayEvent.getStart().plusDays(6));
        assertEquals(1, currCheck.size());

        // Saturday
        assertEquals(start1, currCheck.get(0).getStart());
        assertEquals(start1.plusHours(2), currCheck.get(0).getEnd());

        System.out.println("\n\t- Test createEvent - Once a week event, check 2 full week");
        currCheck = generate.generateEvents(oneDayEvent, oneDayEvent.getStart().minusDays(1),
                oneDayEvent.getStart().plusDays(13));
        assertEquals(2, currCheck.size());

        // Saturday - 1st week
        assertEquals(start1, currCheck.get(0).getStart());
        assertEquals(start1.plusHours(2), currCheck.get(0).getEnd());

        // Saturday - 2nd week
        assertEquals(start1.plusDays(7), currCheck.get(1).getStart());
        assertEquals(start1.plusDays(7).plusHours(2), currCheck.get(1).getEnd());

        System.out.println("\n\t- Test createEvent - Once a week, sunday ending monday (nothing should come up)");
        currCheck = generate.generateEvents(oneDayEvent, oneDayEvent.getStart().plusDays(1),
                oneDayEvent.getStart().plusDays(5));
        assertEquals(0, currCheck.size());

        System.out.println("\n\t- Test createEvent - starting Saturday after the time ");
        // it should come up since it only cares about the day of the week
        currCheck = generate.generateEvents(oneDayEvent, oneDayEvent.getStart().plusHours(5),
                oneDayEvent.getStart().plusDays(6));
        assertEquals(1, currCheck.size());

        // Saturday
        assertEquals(start1, currCheck.get(0).getStart());
        assertEquals(start1.plusHours(2), currCheck.get(0).getEnd());
    }

    @Test
    public void testTwoDayGenerateEvents() {
        DateTime start2 = new DateTime(2022, 1, 4, 22, 30);
        User user1 = new User("user1", 1);

        HashSet<Integer> twoDays = new HashSet<>();
        twoDays.add(DateTimeConstants.TUESDAY);
        twoDays.add(DateTimeConstants.FRIDAY);

        RecurringEventGenerator twoDaysEvent = new RecurringEventGenerator("Recurring", 1, user1,
                "My house", start2, start2.plusHours(8), twoDays);

        ArrayList<RecurringEvent> currCheck;

        System.out.println("\n\t- Test createEvent - Two days, with overnight, Monday to Friday");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().minusDays(1),
                twoDaysEvent.getStart().plusDays(4));
        assertEquals(2, currCheck.size());

        // Tuesday
        assertEquals(start2, currCheck.get(0).getStart());
        assertEquals(start2.plusHours(8), currCheck.get(0).getEnd());

        // Friday
        assertEquals(start2.plusDays(3), currCheck.get(1).getStart());
        assertEquals(start2.plusDays(3).plusHours(8), currCheck.get(1).getEnd());

        System.out.println("\n\t- Test createEvent - Two days, including only half of the recurring events, Monday to Wednesday");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().minusDays(1),
                twoDaysEvent.getStart().plusDays(1));
        assertEquals(1, currCheck.size());

        // Tuesday
        assertEquals(start2, currCheck.get(0).getStart());
        assertEquals(start2.plusHours(8), currCheck.get(0).getEnd());

        System.out.println("\n\t- Test createEvent - Two days, smaller window, Tuesday to Wednesday");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart(),
                twoDaysEvent.getStart().plusDays(1));
        assertEquals(1, currCheck.size());

        // Tuesday
        assertEquals(start2, currCheck.get(0).getStart());
        assertEquals(start2.plusHours(8), currCheck.get(0).getEnd());

        System.out.println("\n\t- Test createEvent - Two days, other half of the recurring events, Thursday to Saturday");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().plusDays(2),
                twoDaysEvent.getStart().plusDays(4));
        assertEquals(1, currCheck.size());

        // Saturday
        assertEquals(start2.plusDays(3), currCheck.get(0).getStart());
        assertEquals(start2.plusDays(3).plusHours(8), currCheck.get(0).getEnd());

        System.out.println("\n\t- Test createEvent - Two days, smaller window, Friday to Saturday");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().plusDays(3),
                twoDaysEvent.getStart().plusDays(4));
        assertEquals(1, currCheck.size());

        // Saturday
        assertEquals(start2.plusDays(3), currCheck.get(0).getStart());
        assertEquals(start2.plusDays(3).plusHours(8), currCheck.get(0).getEnd());


        System.out.println("\n\t- Test createEvent - Two days, a week after started the recurring event");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().plusDays(6),
                twoDaysEvent.getStart().plusDays(20));
        assertEquals(4, currCheck.size());

        // Tuesday - 1st week
        assertEquals(start2.plusDays(7), currCheck.get(0).getStart());
        assertEquals(start2.plusDays(7).plusHours(8), currCheck.get(0).getEnd());

        // Saturday - 1st week
        assertEquals(start2.plusDays(10), currCheck.get(1).getStart());
        assertEquals(start2.plusDays(10).plusHours(8), currCheck.get(1).getEnd());

        // Tuesday - 2nd week
        assertEquals(start2.plusDays(14), currCheck.get(2).getStart());
        assertEquals(start2.plusDays(14).plusHours(8), currCheck.get(2).getEnd());

        // Saturday - 2nd week
        assertEquals(start2.plusDays(17), currCheck.get(3).getStart());
        assertEquals(start2.plusDays(17).plusHours(8), currCheck.get(3).getEnd());

        System.out.println("\n\t- Test createEvent - Two days, week and a half");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().minusDays(1),
                twoDaysEvent.getStart().plusDays(9));
        assertEquals(3, currCheck.size());

        // Tuesday - 1st week
        assertEquals(start2, currCheck.get(0).getStart());
        assertEquals(start2.plusHours(8), currCheck.get(0).getEnd());

        // Saturday- 1st week
        assertEquals(start2.plusDays(3), currCheck.get(1).getStart());
        assertEquals(start2.plusDays(3).plusHours(8), currCheck.get(1).getEnd());

        // Tuesday - 2nd week
        assertEquals(start2.plusDays(7), currCheck.get(2).getStart());
        assertEquals(start2.plusDays(7).plusHours(8), currCheck.get(2).getEnd());

        System.out.println("\n\t- Test createEvent - Two days, full week starting Wednesday, Wednesday to Wednesday");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().plusDays(1),
                twoDaysEvent.getStart().plusDays(8));
        assertEquals(2, currCheck.size());

        // Saturday - 1st week
        assertEquals(start2.plusDays(3), currCheck.get(0).getStart());
        assertEquals(start2.plusDays(3).plusHours(8), currCheck.get(0).getEnd());

        // Tuesday - 2nd week
        assertEquals(start2.plusDays(7), currCheck.get(1).getStart());
        assertEquals(start2.plusDays(7).plusHours(8), currCheck.get(1).getEnd());

        System.out.println("\n\t- Test createEvent - Two days, Week and a half starting Thursday,Thursday till next Sunday");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().plusDays(2),
                twoDaysEvent.getStart().plusDays(12));
        assertEquals(3, currCheck.size());

        // Saturday - 1st week
        assertEquals(start2.plusDays(3), currCheck.get(0).getStart());
        assertEquals(start2.plusDays(3).plusHours(8), currCheck.get(0).getEnd());

        // Tuesday - 2nd week
        assertEquals(start2.plusDays(7), currCheck.get(1).getStart());
        assertEquals(start2.plusDays(7).plusHours(8), currCheck.get(1).getEnd());

        // Saturday - 2nd week
        assertEquals(start2.plusDays(10), currCheck.get(2).getStart());
        assertEquals(start2.plusDays(10).plusHours(8), currCheck.get(2).getEnd());

        System.out.println("\n\t- Test createEvent - Two days, Nothing on the interval, Saturday to Monday");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().plusDays(4),
                twoDaysEvent.getStart().plusDays(6));
        assertEquals(0, currCheck.size());

        System.out.println("\n\t- Test createEvent - Two days, interval with no recurring events");
        currCheck = generate.generateEvents(twoDaysEvent, twoDaysEvent.getStart().plusDays(4),
                twoDaysEvent.getStart().plusDays(6));
        assertEquals(0, currCheck.size());

    }

    @Test
    public void testEverydayGenerateEvents() {
        System.out.println("\n- Testing: GenerateRecurringEvents - EdgeCaseGenerateEvents");
        DateTime start3 = new DateTime(2022, 1, 9, 11, 30);

        User user1 = new User("user1", 1);
        HashSet<Integer> allDays = new HashSet<>();

        ArrayList<RecurringEvent> currCheck;

        allDays.add(DateTimeConstants.MONDAY);
        allDays.add(DateTimeConstants.TUESDAY);
        allDays.add(DateTimeConstants.WEDNESDAY);
        allDays.add(DateTimeConstants.THURSDAY);
        allDays.add(DateTimeConstants.FRIDAY);
        allDays.add(DateTimeConstants.SATURDAY);
        allDays.add(DateTimeConstants.SUNDAY);

        RecurringEventGenerator everyDayEvent = new RecurringEventGenerator("Recurring", 1, user1,
                "My house", start3, start3.plusHours(6), allDays);

        System.out.println("\n\t- Test createEvent - Everyday event, Full week");
        currCheck = generate.generateEvents(everyDayEvent, everyDayEvent.getStart(),
                everyDayEvent.getStart().plusDays(6));
        assertEquals(7, currCheck.size());

        checkEveryday(currCheck, start3, 6);

        System.out.println("\n\t- Test createEvent - Everyday event, A month");
        currCheck = generate.generateEvents(everyDayEvent, everyDayEvent.getStart(),
                everyDayEvent.getStart().plusDays(30));
        assertEquals(31, currCheck.size());

        checkEveryday(currCheck, start3, 6);

        System.out.println("\n\t- Test createEvent - Everyday event, A year");
        currCheck = generate.generateEvents(everyDayEvent, everyDayEvent.getStart(),
                everyDayEvent.getStart().plusDays(364));
        assertEquals(365, currCheck.size());

        checkEveryday(currCheck, start3, 6);


        System.out.println("\n- Completed Testing: GenerateRecurringEvents - EdgeCaseGenerateEvents");
    }

    private void checkEveryday(ArrayList<RecurringEvent> dateList, DateTime start, int hours) {

        for (int i = 0; i < dateList.size(); i++) {

            //daylight savings
            if (dateList.get(i).getStart().getDayOfMonth() == 13 &&
                    dateList.get(i).getStart().getMonthOfYear() == 3) {
                assertEquals(start.plusDays(i).plusHours(1), dateList.get(i).getStart());
                assertEquals(start.plusDays(i).plusHours(hours + 1), dateList.get(i).getEnd());
            } else if (dateList.get(i).getStart().getDayOfMonth() == 6 &&
                    dateList.get(i).getStart().getMonthOfYear() == 11) {
                assertEquals(start.plusDays(i).minusHours(1), dateList.get(i).getStart());
                assertEquals(start.plusDays(i).plusHours(hours - 1), dateList.get(i).getEnd());
            } else {
                assertEquals(start.plusDays(i), dateList.get(i).getStart());
                assertEquals(start.plusDays(i).plusHours(hours), dateList.get(i).getEnd());
            }
        }

    }

    @Test
    public void testInvalidCases() {

        DateTime start = new DateTime(2022, 1, 1, 10, 30);
        User user1 = new User("user1", 1);

        HashSet<Integer> oneDay = new HashSet<>();
        oneDay.add(DateTimeConstants.SUNDAY);

        RecurringEventGenerator event = new RecurringEventGenerator("Recurring", 1, user1,
                "My house", start, start.plusHours(2), oneDay);

        System.out.println("\n\t- Test getIntervalFromDefault - NullInput");
        assertNull(generate.getIntervalFromDefault(null, new Interval(start, start.plusHours(1))));
        assertNull(generate.getIntervalFromDefault(start, null));
        assertNull(generate.getIntervalFromDefault(null, null));

        // negative interval
        try {
            generate.getIntervalFromDefault(start, new Interval(start, start.minusYears(1)));
            fail();
        } catch (Exception e) {
            assertTrue(true);
        }

        System.out.println("\n\t- Test generateEvents - NullInput");
        assertTrue(generate.generateEvents(null, start, start.plusHours(1)).isEmpty());
        assertTrue(generate.generateEvents(event, null, start.plusHours(1)).isEmpty());
        assertTrue(generate.generateEvents(event, start, null).isEmpty());
        assertTrue(generate.generateEvents(null, null, null).isEmpty());
    }

}

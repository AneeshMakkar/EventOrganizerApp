package comp3350.plarty.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;
import java.util.HashSet;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.User;

public class RecurringEventGeneratorTest {

    String name, location;
    int id;
    User organiser;
    DateTime start_time, end_time;
    RecurringEventGenerator event, event_2;
    HashSet<Integer> days_of_week, days_of_week_2;

    @Before
    public void setUp() {
        name = "testEvent";
        id = 1;
        organiser = new User("Mia", 0);
        location = "Winnipeg";
        start_time = new DateTime(2022, 10, 12, 10, 30);
        end_time = new DateTime(2022, 10, 12, 13, 45);
        days_of_week = new HashSet<>();
    }

    @Test
    public void testTypicalCasesGetters() {
        System.out.println("\n- Testing: RecurringEventGeneratorTest - Getters");
        days_of_week.add(DateTimeConstants.FRIDAY);

        event = new RecurringEventGenerator(name, id, organiser, location, start_time, end_time, days_of_week);

        System.out.println("\n- Testing: getDaysOfWeek");
        assertEquals(1, event.getDaysOfWeek().size());
        assertTrue(event.getDaysOfWeek().contains(DateTimeConstants.FRIDAY));

        System.out.println("\n- Testing: getAlterations");
        assertEquals(0, event.getAlterations().size());

        System.out.println("\n- Testing: getAlteration");
        DateTime alteration = new DateTime(2022, 11, 14, 10, 0);
        Interval alterationInterval = new Interval(start_time.minusMinutes(40), end_time.minusMinutes(30));
        event.createAlteration(alteration, alterationInterval);

        assertEquals(1, event.getAlterations().size());
        assertEquals(alterationInterval, event.getAlteration(alteration));

        System.out.println("\n- Completed Testing: RecurringEventGeneratorTest - Getters");
    }

    @Test
    public void testTypicalCasesSetters() {
        System.out.println("\n- Testing: RecurringEventGeneratorTest - Setters");
        event = new RecurringEventGenerator("Im a recurrent Event", id, organiser, "your house",
                start_time.plusHours(2), end_time.plusHours(3), days_of_week);

        System.out.println("\n- Testing: getDaysOfWeek");

        days_of_week_2 = new HashSet<>();
        days_of_week_2.add(DateTimeConstants.MONDAY);

        event.setDaysOfWeek(days_of_week_2);
        assertEquals(1, event.getDaysOfWeek().size());
        assertEquals(days_of_week_2, event.getDaysOfWeek());

        days_of_week.add(DateTimeConstants.TUESDAY);
        days_of_week.add(DateTimeConstants.WEDNESDAY);
        days_of_week.add(DateTimeConstants.THURSDAY);

        event.setDaysOfWeek(days_of_week);
        assertEquals(3, event.getDaysOfWeek().size());
        assertEquals(days_of_week, event.getDaysOfWeek());

        System.out.println("\n- Completed Testing: RecurringEventGeneratorTest - Setters");
    }

    @Test
    public void testInvalidSetters() {
        System.out.println("\n- Testing: RecurringEventGeneratorTest - Invalid Setters");

        event = new RecurringEventGenerator("Im a name", id, organiser, "not your house",
                start_time.plusHours(2), end_time.plusHours(3), days_of_week);

        System.out.println("\n- Testing: setDaysOfWeek - Null Days of the week");
        try {
            event.setDaysOfWeek(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setDaysOfWeek - Negative week day");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(-1);
            event.setDaysOfWeek(days_of_week_2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setDaysOfWeek - 0 week day");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(0);
            event.setDaysOfWeek(days_of_week_2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setDaysOfWeek - 8th Week day");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(8);
            event.setDaysOfWeek(days_of_week_2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setDaysOfWeek - 1000th Week day");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(1000);
            event.setDaysOfWeek(days_of_week_2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setDaysOfWeek - adding a Monday and Week Day 10");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(1);
            days_of_week_2.add(10);
            event.setDaysOfWeek(days_of_week_2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Completed Testing: RecurringEventGeneratorTest - Invalid Setters");
    }

    @Test
    public void testInvalidCreation() {
        System.out.println("\n- Testing: RecurringEventGeneratorTest - Invalid Creation");

        System.out.println("\n- Testing: Constructor - Null Days of the week");
        try {
            event_2 = new RecurringEventGenerator(name, id, organiser, location, start_time, end_time, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Negative Week Day");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(-3);
            event_2 = new RecurringEventGenerator(name, id, organiser, location, start_time, end_time, days_of_week_2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - 0 Week Day");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(0);
            event_2 = new RecurringEventGenerator(name, id, organiser, location, start_time, end_time, days_of_week_2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - 8th Week Day");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(8);
            event_2 = new RecurringEventGenerator(name, id, organiser, location, start_time, end_time, days_of_week_2);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - adding sunday and week day 8");
        try {
            days_of_week_2 = new HashSet<>();
            days_of_week_2.add(7);
            days_of_week_2.add(8);
            event_2 = new RecurringEventGenerator(name, id, organiser, location, start_time, end_time, days_of_week_2);
            fail();

        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Completed Testing: RecurringEventGeneratorTest - Invalid Creation");
    }

    @Test
    public void testTypicalAlterations() {
        System.out.println("\n- Testing: RecurringEventGeneratorTest - Typical Alterations");

        event = new RecurringEventGenerator("Im going to have alterations8", id, organiser, "not your house",
                start_time.plusHours(2), end_time.plusHours(3), days_of_week);

        System.out.println("\n- Testing: createAlteration - Regular additions");
        event.createAlteration(start_time, new Interval (start_time, end_time));
        assertEquals( new Interval (start_time, end_time), event.getAlteration(start_time));

        event.createAlteration(end_time.plusHours(2), new Interval (start_time, end_time.plusMinutes(30)));
        assertEquals( new Interval (start_time, end_time.plusMinutes(30)),
                event.getAlteration(end_time.plusHours(2)));

        System.out.println("\n- Testing: createAlteration - Clearing additions");
        event.clearAlterations();

        System.out.println("\n- Testing: createAlteration - The keys return null");
        assertNull(event.getAlteration(start_time));
        assertNull(event.getAlteration(end_time.plusHours(2)));

        System.out.println("\n- Completed Testing: RecurringEventGeneratorTest - Typical Alterations");
    }

    @Test
    public void testEdgeCasesAlterations() {
        System.out.println("\n- Testing: RecurringEventGeneratorTest - edgeCasesAlterations");

        event = new RecurringEventGenerator("Im a name", id, organiser, "not your house",
                start_time.plusHours(2), end_time.plusHours(3), days_of_week);

        System.out.println("\n- Testing: createAlteration - 0 min alteration");
        event.createAlteration(start_time, new Interval (start_time, start_time));
        assertEquals( new Interval (start_time, start_time), event.getAlteration(start_time));

        System.out.println("\n- Testing: createAlteration - same start time with different interval");
        event.createAlteration(start_time, new Interval (start_time, end_time));
        assertEquals( new Interval (start_time, end_time), event.getAlteration(start_time));

        System.out.println("\n- Completed Testing: RecurringEventGeneratorTest - edgeCasesAlterations");
    }

}

package comp3350.plarty.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;

import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.RecurringEvent;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.User;

public class RecurringEventTest {

    String name, location;
    int id;
    User organiser;
    DateTime start_time, end_time;
    RecurringEvent event;
    RecurringEventGenerator event_parent;
    HashSet<Integer> days_of_week;

    @Before
    public void setUp() {
        name = "testEvent";
        id = 1;
        organiser = new User("Mia", 0);
        location = "Winnipeg";
        start_time = new DateTime(2022, 10, 12, 10, 30);
        end_time = new DateTime(2022, 10, 12, 13, 45);
        days_of_week = new HashSet<>();

        event_parent = new RecurringEventGenerator(name, id, organiser, location, start_time, end_time, days_of_week);
        event = new RecurringEvent(name, id, organiser, location, start_time, end_time, event_parent);
    }

    @Test
    public void testTypicalCasesGetters() {
        System.out.println("\n- Testing: RecurringEventTest - Getters");

        System.out.println("\n- Testing: getParent");
        assertEquals(event_parent, event.getParent());

        System.out.println("\n- Completed Testing: RecurringEventTest - Getters");
    }

    @Test
    public void testTypicalCreation() {
        System.out.println("\n- Testing: RecurringEventTest - Typical Creation");

        System.out.println("\n- Testing: Constructor");
        assertEquals(name, event.getName());
        assertEquals(id, event.getId());
        assertEquals(organiser, event.getOrganiser());
        assertEquals(location, event.getLocation());
        assertEquals(start_time, event.getStart());
        assertEquals(end_time, event.getEnd());
        assertEquals(new Interval(start_time, end_time), event.getInterval());

        System.out.println("\n- Completed Testing: RecurringEventTest - Typical Creation");

    }

    @Test
    public void testInvalidCreation() {
        System.out.println("\n- Testing: RecurringEventTest - Invalid Creation");

        System.out.println("\n- Testing: Constructor Null -  Parent");
        try {
            event = new RecurringEvent(name, id, organiser, location, start_time, end_time, null);
            fail();
        }
        catch(IllegalArgumentException e) {
            assertTrue(true);
        }
        System.out.println("\n- Completed Testing: RecurringEventTest - Invalid Creation");
    }

    @Test
    public void testTypicalCasesEquals() {
        System.out.println("\n- Testing: RecurringEventTest - typicalCasesEquals");
        HashSet<Integer> days = new HashSet<>();
        days.add(DateTimeConstants.WEDNESDAY);

        RecurringEventGenerator parent = new RecurringEventGenerator("Im a parent", 2, organiser, "UofM",
                start_time.plusHours(2), end_time.plusHours(2), days);
        Event child1 = new RecurringEvent("hello", 3, organiser, "UofM",
                start_time.plusHours(3), end_time.plusHours(3), parent);
        Event child2 = new RecurringEvent("hello", 3, organiser, "UofM",
                start_time.plusHours(5), end_time.plusHours(5), parent);

        System.out.println("\n- Testing: equals - Equals is true");
        assertEquals(event, event);
        assertEquals(child1, child1);
        assertEquals(child2, child2);

        System.out.println("\n- Testing: equals - Equals is false");
        assertNotEquals(event, child1);
        assertNotEquals(child1, event);

        assertNotEquals(child1, child2);
        assertNotEquals(child2, child1);

        assertNotEquals(child2, event);
        assertNotEquals(event, child2);

        System.out.println("\n- Completed Testing: RecurringEventTest - typicalCasesEquals");
    }

    @Test
    public void testEdgeCasesEquals() {
        System.out.println("\n- Testing: RecurringEventTest - edgeCasesEquals");

        HashSet<Integer> days = new HashSet<>();
        days.add(DateTimeConstants.FRIDAY);

        RecurringEventGenerator parent = new RecurringEventGenerator("Im a parent", 2, organiser, "UofM",
                start_time.plusHours(2), end_time.plusHours(2), days);
        Event child1 = new RecurringEvent("hello", 3, organiser, "UofM",
                start_time.plusHours(3), end_time.plusHours(3), parent);
        Event child2 = new RecurringEvent("hello", 4, organiser, "UofM",
                start_time.plusHours(5), end_time.plusHours(5), parent);

        System.out.println("\n- Testing: equals - Comparing child to paren");
        assertNotEquals(parent, child1);
        assertNotEquals(child1, parent);

        assertNotEquals(parent, child2);
        assertNotEquals(child2, parent);

        System.out.println("\n- Testing: equals - Equals on Null");
        assertNotEquals(child1, null);
        assertNotEquals(null, child1);

        System.out.println("\n- Completed Testing: RecurringEventTest - edgeCasesEquals");
    }

    @Test
    public void testToString() {
        System.out.println("\n- Testing: EventTest - ToString");
        HashSet<Integer> days = new HashSet<>();
        days.add(DateTimeConstants.SATURDAY);

        RecurringEventGenerator parent = new RecurringEventGenerator("Im a parent uwu", 2, organiser, "UofM",
                start_time.plusHours(2), end_time.plusHours(2), days);
        Event child1 = new RecurringEvent("hellowo", 3, organiser, "UofM",
                start_time.plusHours(3), end_time.plusHours(3), parent);
        Event child2 = new RecurringEvent("helluwu", 4, organiser, "UofM",
                start_time.plusHours(5), end_time.plusHours(5), parent);


        System.out.println("\n- Testing: toString - testing Recurring Event Child");
        assertEquals("\n Event Name: hellowo\n" +
                "\tID: 3\n" +
                "\tOrganiser Name: Mia\n" +
                "\tLocation: UofM\n" +
                " Instance of Im a parent uwu at 2022-10-12T13:30:00.000-05:00 to 2022-10-12T16:45:00.000-05:00", child1.toString());

        System.out.println("\n- Testing: toString - testing anotherRecurring Event Child");
        assertEquals("\n Event Name: helluwu\n" +
                "\tID: 4\n" +
                "\tOrganiser Name: Mia\n" +
                "\tLocation: UofM\n" +
                " Instance of Im a parent uwu at 2022-10-12T15:30:00.000-05:00 to 2022-10-12T18:45:00.000-05:00", child2.toString());

        System.out.println("\n- Completed Testing: EventTest - ToString");
    }

}

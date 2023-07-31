package comp3350.plarty.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.Test;

import java.util.HashSet;

import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.RecurringEvent;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;

public class EventTest {

    String name, location;
    int id;
    User organiser;
    DateTime startTime, endTime;
    Event event;

    @Test
    public void testTypicalCasesGetters() {
        System.out.println("\n- Testing: EventTest - Getters");

        name = "testEvent";
        id = 1;
        organiser = new User("Mia", 0);
        location = "Winnipeg";
        startTime = new DateTime(2022, 10, 12, 10, 30);
        endTime = new DateTime(2022, 10, 12, 13, 45);

        event = new SingleEvent(name, id, organiser, location, startTime, endTime);

        System.out.println("\n- Testing: getName");
        assertEquals(name, event.getName());

        System.out.println("\n- Testing: getId");
        assertEquals(id, event.getId());

        System.out.println("\n- Testing: getOrganiser");
        assertEquals(organiser, event.getOrganiser());

        System.out.println("\n- Testing: getLocation");
        assertEquals(location, event.getLocation());

        System.out.println("\n- Testing: getStart");
        assertEquals(startTime, event.getStart());

        System.out.println("\n- Testing: getEnd");
        assertEquals(endTime, event.getEnd());

        System.out.println("\n- Testing: getInterval");
        assertEquals(new Interval(startTime, endTime), event.getInterval());

        System.out.println("\n- Completed Testing: EventTest - Getters");
    }

    @Test
    public void testTypicalCasesSetters() {
        System.out.println("\n- Testing: EventTest - Setters");

        name = "diff test";
        organiser = new User("Chris", 1);
        location= "Gimli";
        startTime = new DateTime(2022, 10, 12, 11, 30);
        endTime = new DateTime(2022, 10, 12, 14, 30);

        event = new SingleEvent(name, id, organiser, location, startTime, endTime);

        name = "I am a new name";
        organiser = new User("def not Chris", 1);
        location= "Morden";
        DateTime startTime2 = new DateTime(2022, 10, 10, 11, 30);
        DateTime endTime2 = new DateTime(2022, 10, 10, 12, 6);

        System.out.println("\n- Testing: setName");
        event.setName(name);
        assertEquals(name, event.getName());

        System.out.println("\n- Testing: setOrganiser");
        event.setOrganiser(organiser);
        assertEquals(organiser, event.getOrganiser());

        System.out.println("\n- Testing: setLocation");
        event.setLocation(location);
        assertEquals(location, event.getLocation());

        System.out.println("\n- Testing: setStart");
        event.setStart(startTime2);
        assertEquals(startTime2, event.getStart());
        assertEquals(new Interval(startTime2, endTime), event.getInterval());

        System.out.println("\n- Testing: setEnd");
        event.setEnd(endTime2);
        assertEquals(endTime2, event.getEnd());
        assertEquals(new Interval(startTime2, endTime2), event.getInterval());

        System.out.println("\n- Completed Testing: EventTest - Setters");
    }

    @Test
    public void testInvalidSetters() {
        System.out.println("\n- Testing: EventTest - Invalid Setters");

        name = "Checking this doesnt work";
        organiser = new User("Woops", 1);
        location= "I should go home";
        startTime = new DateTime(2022, 10, 12, 11, 30);
        endTime = new DateTime(2022, 10, 12, 14, 30);

        event = new SingleEvent(name, id, organiser, location, startTime, endTime);

        System.out.println("\n- Testing: setName - Empty Name");
        try {
            event.setName("");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setName - Blank space");
        try {
            event.setName("         ");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setName - New lines");
        try {
            event.setName("\n\n\n");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setName - Null name");
        try {
            event.setName(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setOrganiser - Null Organiser");
        try {
            event.setOrganiser(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Blank Location");
        try {
            event.setLocation("    ");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setLocation - New lines");
        try {
            event.setLocation("\n\n\n");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setLocation - Null Location");
        try {
            event.setLocation(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setStart - Set start as end time");
        event.setStart(endTime);
        assertEquals(startTime, event.getStart());

        System.out.println("\n- Testing: setStart - Set start after end time");
        event.setStart(endTime.plusHours(3));
        assertEquals(startTime, event.getStart());


        System.out.println("\n- Testing: setStart - Null Start");
        try {
            event.setStart(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setEnd - Set end time as start time");
        event.setEnd(startTime);
        assertEquals(endTime, event.getEnd());

        System.out.println("\n- Testing: setStart - Set start time after end time");
        event.setStart(startTime.plusHours(10));
        assertEquals(endTime, event.getEnd());

        System.out.println("\n- Testing: setEnd - Null End");
        try {
            event.setEnd(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Completed Testing: EventTest - Invalid Setters");
    }

    @Test
    public void testInvalidCreation() {
        System.out.println("\n- Testing: EventTest - Invalid Creation");

        startTime = new DateTime(2022, 10, 10, 11, 30);
        endTime = new DateTime(2022, 10, 19, 14, 30);

        System.out.println("\n- Testing: Constructor - Empty name");
        try {
            event = new SingleEvent("", id, organiser, location, startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Blank name");
        try {
            event = new SingleEvent("    ", id, organiser, location, startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Null name");
        try {
            event = new SingleEvent(null, id, organiser, location, startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - New lines name");
        try {
            event = new SingleEvent("\n\n\n", id, organiser, location, startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Null Organizer");
        try {
            event = new SingleEvent(name, id, null, location, startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Empty Location");
        try {
            event = new SingleEvent(name, id, organiser, "", startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);

        }

        System.out.println("\n- Testing: Constructor - Blank Location");
        try {
            event = new SingleEvent(name, id, organiser, "      ", startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - New Line Location");
        try {
            event = new SingleEvent(name, id, organiser, "\n\n\n\n", startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Null Location");
        try {
            event = new SingleEvent(name, id, organiser, null, startTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);

        }

        System.out.println("\n- Testing: Constructor - Same Start and End");
        try {
            event = new SingleEvent(name, id, organiser, location, endTime, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Start after End");
        try {
            event = new SingleEvent(name, id, organiser, location, endTime.minusHours(2), startTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Null Start");
        try {
            event = new SingleEvent(name, id, organiser, location, null, endTime);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Null End");
        try {
            event = new SingleEvent(name, id, organiser, location, startTime, null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Completed Testing: EventTest - Invalid Creation");
    }

    @Test
    public void testTypicalCasesEquals() {
        System.out.println("\n- Testing: EventTest - typicalCasesEquals");
        HashSet<Integer> days = new HashSet<>();
        days.add(DateTimeConstants.MONDAY);

        name = "Let's get testy";
        organiser = new User("Berb", 1);
        location= "Memezuela";
        startTime = new DateTime(2022, 12, 1, 11, 30);
        endTime = new DateTime(2022, 12, 1, 12, 30);

        RecurringEventGenerator eventsGenerated = new RecurringEventGenerator(name, 2, organiser, location,
                startTime, endTime, days);
        Event eventGeneratedInstance = new RecurringEvent("hello", 3, organiser,
                "UofM", startTime, endTime, eventsGenerated);

        System.out.println("\n- Testing: equals - Equals is true");
        assertEquals(event, event);
        assertEquals(eventsGenerated, eventsGenerated);
        assertEquals(eventGeneratedInstance, eventGeneratedInstance);

        System.out.println("\n- Testing: equals - Equals is false");
        assertNotEquals(event, eventsGenerated);
        assertNotEquals(eventsGenerated, event);

        assertNotEquals(eventsGenerated, eventGeneratedInstance);
        assertNotEquals(eventGeneratedInstance, eventsGenerated);

        assertNotEquals(eventGeneratedInstance, event);
        assertNotEquals(event, eventGeneratedInstance);

        System.out.println("\n- Completed Testing: EventTest - typicalCasesEquals");
    }

    @Test
    public void testEdgeCasesEquals() {
        System.out.println("\n- Testing: EventTest - EdgeCasesEquals");

        HashSet<Integer> days = new HashSet<>();
        days.add(DateTimeConstants.SATURDAY);
        name = "Checking this doesnt work";
        organiser = new User("Woops", 1);
        location= "I should go home";
        startTime = new DateTime(2022, 10, 12, 11, 30);
        endTime = new DateTime(2022, 10, 12, 14, 30);

        event = new SingleEvent(name, id, organiser, location, startTime, endTime);

        RecurringEventGenerator eventsGenerated = new RecurringEventGenerator("Im a parent",
                2, organiser, "UofM", startTime, endTime, days);
        Event eventGeneratedInstance1 = new RecurringEvent("hello", 3,
                organiser, "UofM", startTime.plusDays(2), endTime.plusDays(2), eventsGenerated);
        Event eventGeneratedInstance2 = new RecurringEvent("hello", 4,
                organiser, "UofM", startTime.plusDays(1), endTime.plusDays(2), eventsGenerated);

        System.out.println("\n- Testing: equals - Equaling 2 children of the same parent with different id");
        assertNotEquals(eventGeneratedInstance1, eventGeneratedInstance2);
        assertNotEquals(eventGeneratedInstance2, eventGeneratedInstance1);

        System.out.println("\n- Testing: equals - Equals on Null");
        assertNotEquals(event, null);
        assertNotEquals(null, event);

        assertNotEquals(eventGeneratedInstance1, null);
        assertNotEquals(null, eventGeneratedInstance1);

        assertNotEquals(eventGeneratedInstance2, null);
        assertNotEquals(null, eventGeneratedInstance2);

        System.out.println("\n- Completed Testing: EventTest - EdgeCasesEquals");
    }

    @Test
    public void testToString() {
        System.out.println("\n- Testing: EventTest - ToString");
        HashSet<Integer> days = new HashSet<>();
        days.add(DateTimeConstants.SATURDAY);

        name = "Let's get stringy";
        organiser = new User("Berbzzz", 1);
        location= "Do you like eggs?";
        startTime = new DateTime(2022, 12, 12, 12, 12);
        endTime = new DateTime(2022, 12, 12, 12, 21);

        event = new SingleEvent(name, id, organiser, location, startTime, endTime);

        RecurringEventGenerator eventsGenerated = new RecurringEventGenerator(name, 2, organiser, location,
                startTime, endTime, days);
        Event eventGeneratedInstance = new RecurringEvent("ññññññ", 3, organiser,
                "Batcave", startTime, endTime, eventsGenerated);

        System.out.println("\n- Testing: toString - testing Single Event");
        assertEquals("\n Event Name: Let's get stringy\n" +
                "\tID: 0\n" +
                "\tOrganiser Name: Berbzzz\n" +
                "\tLocation: Do you like eggs?", event.toString());

        System.out.println("\n- Testing: toString - testing Recurring Event Parent");
        assertEquals("\n Event Name: Let's get stringy\n" +
                "\tID: 2\n" +
                "\tOrganiser Name: Berbzzz\n" +
                "\tLocation: Do you like eggs?", eventsGenerated.toString());

        System.out.println("\n- Testing: toString - testing Recurring Event Child");
        assertEquals("\n Event Name: ññññññ\n" +
                "\tID: 3\n" +
                "\tOrganiser Name: Berbzzz\n" +
                "\tLocation: Batcave\n" +
                " Instance of Let's get stringy at 2022-12-12T12:12:00.000-06:00 to 2022-12-12T12:21:00.000-06:00", eventGeneratedInstance.toString());

        System.out.println("\n- Completed Testing: EventTest - ToString");
    }

}

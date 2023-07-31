package comp3350.plarty.tests.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Interval;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import comp3350.plarty.application.Main;
import comp3350.plarty.application.Services;
import comp3350.plarty.business.AccessRecurringEvents;
import comp3350.plarty.business.AccessSingleEvents;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.business.GenerateRecurringEvents;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.RecurringEvent;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;
import comp3350.plarty.persistence.DataAccess;
import comp3350.plarty.persistence.DataAccessStub;

public class BusinessPersistenceSeamTest {
    DataAccess dataAccess;
    AccessRecurringEvents accessRecurringEvents;
    AccessSingleEvents accessSingleEvents;
    GenerateRecurringEvents generateRecurringEvents;
    AccessUsers accessUsers;
    User mainUser;

    @Before
    public void setup() {
        dataAccess = Services.createDataAccess(new DataAccessStub());
        accessRecurringEvents = new AccessRecurringEvents();
        generateRecurringEvents = new GenerateRecurringEvents();
        accessSingleEvents = new AccessSingleEvents();
        accessUsers = new AccessUsers();

        mainUser = accessUsers.getMainUser();

        dataAccess.open(Main.dbName);
    }

    @After
    public void tearDown() {
        dataAccess.close();
    }


    @Test
    public void testEventRetrieval() {
        System.out.println("\n- Testing: Event Retrieval");

        //setup
        DateTime start = new DateTime(2022, 4, 3, 12, 10);
        DateTime end = new DateTime(2022, 4, 3, 14, 30);

        System.out.println("\n\t- Main user Creates an event");
        assertTrue(accessSingleEvents.validEvent("Plarty party", mainUser,
                "Engineering Building", start, end));

        SingleEvent singleEvent = (SingleEvent) accessSingleEvents.createSingleEvent("Plarty party", mainUser,
                "Engineering Building", start, end);

        assertEquals("Plarty party", singleEvent.getName());
        assertEquals(mainUser, singleEvent.getOrganiser());
        assertEquals("Engineering Building", singleEvent.getLocation());
        assertEquals(start, singleEvent.getStart());
        assertEquals(end, singleEvent.getEnd());
        assertTrue(accessSingleEvents.isUserAnOrganizer(singleEvent, mainUser));
        int sizeOwned = accessUsers.getOwnedEvents(mainUser).size();
        assertEquals(singleEvent, accessUsers.getOwnedEvents(mainUser).get(sizeOwned - 1));

        System.out.println("\n\t- Check no one is invited");
        ArrayList<User> userInvited = new ArrayList<>();
        assertEquals(userInvited, accessSingleEvents.getInvitees(singleEvent));

        System.out.println("\n\t- Check no one has answered");
        HashMap<User, InviteResponse> inviteResponses = new HashMap<>();
        assertEquals(inviteResponses, accessSingleEvents.getInviteResponses(singleEvent));

        System.out.println("\n\t- Main user invites an user");
        User invited1 = accessUsers.createUser("Im a test user");
        assertEquals(dataAccess.getAllOtherUsers(invited1.getId()), accessUsers.getAllOtherUsers(invited1.getId()));

        assertFalse(accessUsers.isUserInvited(singleEvent, invited1));

        assertTrue(accessSingleEvents.inviteUser(singleEvent, invited1));
        assertTrue(accessUsers.isUserInvited(singleEvent, invited1));

        System.out.println("\n\t- Check the user is on the list");
        userInvited.add(invited1);
        assertEquals(1, accessUsers.getInvitedEvents(invited1).size());
        assertEquals(singleEvent, accessUsers.getInvitedEvents(invited1).get(0));

        System.out.println("\n\t- The user accepted the invitation");
        accessUsers.getInvitationResponses(invited1).put(singleEvent, InviteResponse.ACCEPTED);
        assertEquals(1, accessUsers.getAttendingEvents(invited1).size());
        assertEquals(singleEvent, accessUsers.getAttendingEvents(invited1).get(0));

        System.out.println("\n\t- Main user edits the event");
        assertTrue(accessSingleEvents.editEvent(mainUser, singleEvent, "Plarty 2",
                "My House", start, end.plusMinutes(30)));

        assertEquals("Plarty 2", singleEvent.getName());
        assertEquals(mainUser, singleEvent.getOrganiser());
        assertEquals("My House", singleEvent.getLocation());
        assertEquals(start, singleEvent.getStart());
        assertEquals(end.plusMinutes(30), singleEvent.getEnd());
        assertEquals(singleEvent, accessUsers.getOwnedEvents(mainUser).get(sizeOwned - 1));

        System.out.println("\n\t- Check the user information is updated in the invitation");
        assertTrue(accessUsers.isUserInvited(singleEvent, invited1));
        assertEquals(1, accessUsers.getInvitedEvents(invited1).size());
        Event event = accessUsers.getInvitedEvents(invited1).get(0);

        assertEquals("Plarty 2", event.getName());
        assertEquals(mainUser, event.getOrganiser());
        assertEquals("My House", event.getLocation());
        assertEquals(start, event.getStart());
        assertEquals(end.plusMinutes(30), event.getEnd());

        System.out.println("\n\t- Party gets cancelled, user is withdrawn");
        assertTrue(accessUsers.isUserInvited(singleEvent, invited1));
        assertTrue(accessSingleEvents.getInvitees(singleEvent).isEmpty());

        System.out.println("\n- Completed Testing: Event Retrieval");
    }


    @Test
    public void testInviteResponses() {
        System.out.println("\n- Testing: Invite Responses");

        //setup
        DateTime start = new DateTime(2022, 5, 4, 12, 10);
        DateTime end = new DateTime(2022, 5, 4, 14, 30);

        System.out.println("\n\t- New user invites the main user to an event");
        User newUser = accessUsers.createUser("Im inviting the main user");
        SingleEvent singleEvent = (SingleEvent) accessSingleEvents.createSingleEvent("Plarty",
                newUser, "U of M", start, end);

        System.out.println("\n\t- Check the main user isn't invited");
        assertFalse(accessUsers.userIsAvailable(newUser, start, end));
        assertFalse(accessUsers.isUserInvited(singleEvent, mainUser));

        System.out.println("\n\t- Invite the main user");
        accessSingleEvents.inviteUser(singleEvent, mainUser);

        System.out.println("\n\t- Default must be NO_RESPONSE");
        SingleEvent currEvent = (SingleEvent) accessUsers.getInvitedEvents(mainUser).get(0);

        assertTrue(accessUsers.isUserInvited(singleEvent, mainUser));
        assertEquals(singleEvent, currEvent);
        assertEquals(InviteResponse.NO_RESPONSE, accessSingleEvents.getInviteResponses(currEvent).get(mainUser));

        System.out.println("\n\t-  Main user accepted");

        accessUsers.getInvitationResponses(mainUser).put(singleEvent, InviteResponse.ACCEPTED);
        assertEquals(InviteResponse.ACCEPTED, accessUsers.getResponseToEvent(mainUser, singleEvent));

        assertTrue(accessUsers.isUserInvited(singleEvent, mainUser));
        assertEquals(InviteResponse.ACCEPTED, accessSingleEvents.getInviteResponses(currEvent).get(mainUser));

        System.out.println("\n\t- Main user changed their mind and declined");
        accessUsers.getInvitationResponses(mainUser).put(singleEvent, InviteResponse.DECLINED);
        assertEquals(InviteResponse.DECLINED, accessUsers.getResponseToEvent(mainUser, singleEvent));

        assertTrue(accessUsers.isUserInvited(singleEvent, mainUser));
        assertEquals(InviteResponse.DECLINED, accessSingleEvents.getInviteResponses(currEvent).get(mainUser));

        System.out.println("\n\t-  Main user schedule clear up now they are attending");
        accessUsers.getInvitationResponses(mainUser).put(singleEvent, InviteResponse.ACCEPTED);
        assertEquals(InviteResponse.ACCEPTED, accessUsers.getResponseToEvent(mainUser, singleEvent));

        assertTrue(accessUsers.isUserInvited(singleEvent, mainUser));
        assertEquals(InviteResponse.ACCEPTED, accessSingleEvents.getInviteResponses(currEvent).get(mainUser));

        System.out.println("\n\t-  Checking if user is invited to null");
        assertFalse(accessUsers.isUserInvited(null, mainUser));

        System.out.println("\n\t- The event got cancelled so the user is not invited");
        accessUsers.getInvitationResponses(mainUser).remove(singleEvent);
        assertFalse(accessUsers.isUserInvited(singleEvent, mainUser));
        assertNotEquals(currEvent, accessUsers.getInvitedEvents(mainUser).get(0));
        assertNull(accessSingleEvents.getInviteResponses(currEvent).get(mainUser));

        System.out.println("\n- Completed Testing: Invite Responses");
    }


    @Test
    public void testScheduleChecking() {
        System.out.println("\n- Testing: Schedule Checking");

        //setup
        DateTime start = new DateTime(2022, 5, 4, 12, 10);
        DateTime end = new DateTime(2022, 5, 4, 14, 30);

        ArrayList<Event> events = new ArrayList<>();

        User johnTwo = accessUsers.createUser("John Two");
        int johnsId = johnTwo.getId();

        System.out.println("\n\t- getSchedule - Nothing on the schedule");
        assertEquals(events, accessUsers.getSchedule(johnTwo, start, end));

        System.out.println("\n\t- John creates an event");
        assertTrue(accessUsers.userIsAvailable(johnTwo, start, end));
        SingleEvent singleEvent = (SingleEvent) accessSingleEvents.createSingleEvent("Plarty",
                accessUsers.getUser(johnsId), "U of M", start, end);
        int plartyId = singleEvent.getId();

        events.add(singleEvent);
        DateTime startHacker = dataAccess.getEvent(plartyId).getStart();
        DateTime endHacker = dataAccess.getEvent(plartyId).getEnd();

        System.out.println("\n\t- John checks is in their schedule");
        System.out.println(accessUsers.getSchedule(johnTwo, startHacker, endHacker));
        assertTrue(accessUsers.getSchedule(johnTwo, startHacker,
                endHacker).contains(dataAccess.getEvent(plartyId)));
        assertEquals(1, accessUsers.getSchedule(johnTwo, startHacker, endHacker).size());

        System.out.println("\n\t- Then he checks a different time that doesn't have anything scheduled");

        assertEquals(0, accessUsers.getSchedule(johnTwo, startHacker.minusHours(4), endHacker.minusHours(4)).size());

        System.out.println("\n\t- They add another event");
        Event afterPlarty = accessSingleEvents.createSingleEvent("After Plarty", accessUsers.getUser(6), "U of M", startHacker.plusHours(5), endHacker.plusHours(5));

        assertTrue(accessSingleEvents.inviteUser((SingleEvent) afterPlarty, johnTwo));
        assertTrue(accessUsers.respondToInvite(johnTwo, afterPlarty, InviteResponse.ACCEPTED));
        assertEquals(1, accessUsers.getSchedule(johnTwo, afterPlarty.getStart().minusHours(1),
                afterPlarty.getEnd().plusHours(1)).size());

        System.out.println("\n\t- A week after there is another afterPlarty event in a week");
        accessUsers.getSchedule(johnTwo, startHacker.minusDays(8), endHacker.plusDays(8));
        assertEquals(2, accessUsers.getSchedule(johnTwo, startHacker.minusDays(8), endHacker.plusDays(8)).size());

        System.out.println("\n\t- Checking  with overlapping event, must include it");
        assertTrue(accessUsers.getSchedule(johnTwo, dataAccess.getEvent(plartyId).getStart().plusMinutes(30),
                dataAccess.getEvent(plartyId).getEnd().minusMinutes(30)).contains(dataAccess.getEvent(plartyId)));

        assertTrue(accessUsers.getSchedule(johnTwo, dataAccess.getEvent(plartyId).getStart().plusHours(1),
                dataAccess.getEvent(plartyId).getEnd().plusHours(3)).contains(dataAccess.getEvent(plartyId)));

        assertTrue(accessUsers.getSchedule(johnTwo, dataAccess.getEvent(plartyId).getStart().minusHours(3),
                dataAccess.getEvent(plartyId).getEnd().minusHours(1)).contains(dataAccess.getEvent(plartyId)));

        System.out.println("\n\t- Deleting the event");
        assertTrue(accessSingleEvents.deleteEvent(plartyId));

        assertEquals(0, accessUsers.getSchedule(johnTwo, start, end).size());
        assertEquals(1, accessUsers.getSchedule(johnTwo, startHacker.plusHours(5),
                endHacker.plusHours(5)).size());


        System.out.println("\n- Completed Testing: typicalGetSchedule");
    }

    @Test
    public void testOvernightActiveHours() {
        System.out.println("\n- Testing: testOvernightActiveHours");

        System.out.println("\n\t- Create a user overnight hours");
        User nightOwl = dataAccess.createUser("Night Owl");

        System.out.println("\n\t- Sets their active hour");
        assertTrue(accessRecurringEvents.setActiveHours(nightOwl, 23, 0, 8, 0));
        assertEquals(8,accessUsers.getInactiveStartHour(nightOwl));
        assertEquals(0,accessUsers.getInactiveStartMinute(nightOwl));

        assertEquals(23,accessUsers.getInactiveEndHour(nightOwl));
        assertEquals(0,accessUsers.getInactiveEndMinute(nightOwl));

        assertEquals(0, accessUsers.getNonInactiveEvents(nightOwl).size());

        System.out.println("\n\t- Test getInactive - overnight hours");
        assertEquals(DateTime.now().getDayOfYear(), accessUsers.getInactive(nightOwl).getStart().getDayOfYear());
        assertEquals(DateTime.now().getDayOfYear(), accessUsers.getInactive(nightOwl).getEnd().getDayOfYear());

        System.out.println("\n\t- Creating an event during inactive hours");
        DateTime invalidStart = new DateTime(2022, 9, 4, 7, 0);
        DateTime invalidEnd = new DateTime(2022, 9, 5, 0, 0);
        assertNull(accessSingleEvents.createSingleEvent("Inactive party", nightOwl, "A dream", invalidStart, invalidEnd));
        assertEquals(0, accessUsers.getNonInactiveEvents(nightOwl).size());

        System.out.println("\n\t- Changing active hours hours");
        assertTrue(accessRecurringEvents.setActiveHours(nightOwl, 22, 0, 7, 0));
        assertEquals(7,accessUsers.getInactiveStartHour(nightOwl));
        assertEquals(0,accessUsers.getInactiveStartMinute(nightOwl));

        assertEquals(22,accessUsers.getInactiveEndHour(nightOwl));
        assertEquals(0,accessUsers.getInactiveEndMinute(nightOwl));

        System.out.println("\n\t- adding an event in the slots that were inactive before");
        Event newEvent = accessSingleEvents.createSingleEvent("New", nightOwl, "I should be there",
                invalidStart.minusMinutes(60), invalidStart.minusMinutes(30));
        assertEquals(6, newEvent.getStart().getHourOfDay());
        assertEquals(6, newEvent.getEnd().getHourOfDay());
        assertEquals(30, newEvent.getEnd().getMinuteOfHour());
        assertEquals(2, accessUsers.getNonInactiveEvents(nightOwl).size());

        System.out.println("\n- Completed Testing:  testOvernightActiveHours");
    }


    @Test
    public void testTwoDayRecurrence() {
        System.out.println("\n- Testing: twoDayRecurrence");

        //setup
        DateTime start = new DateTime(2022, 9, 4, 12, 0);
        DateTime end = new DateTime(2022, 9, 4, 13, 30);

        DateTime childStart = new DateTime(2022, 9, 5, 12, 0);
        DateTime childStart2 = new DateTime(2022, 9, 7, 12, 0);

        DateTime childEnd = new DateTime(2022, 9, 5, 13, 30);
        DateTime childEnd2 = new DateTime(2022, 9, 7, 13, 30);

        HashSet<Integer> twoDays = new HashSet<>();

        twoDays.add(DateTimeConstants.MONDAY);
        twoDays.add(DateTimeConstants.WEDNESDAY);

        System.out.println("\n\t- Test createEvent with two day recurrence");
        RecurringEventGenerator parentTest = (RecurringEventGenerator) accessRecurringEvents.createRecurringEvent("Event", mainUser, "Plarty City", start, end, twoDays, new ArrayList<>());
        int parentId = parentTest.getId();

        RecurringEventGenerator parentTwoDays = new RecurringEventGenerator("Event", parentId, mainUser, "Plarty City", start, end, twoDays);

        assertEquals(parentTwoDays, parentTest);

        System.out.println("\n\t- Test getRecurringEvent with two day recurrence");
        assertEquals(parentTwoDays, accessRecurringEvents.getEvent(parentId));

        System.out.println("\n\t- Test generateEvents with two day recurrence");
        RecurringEvent child1 = new RecurringEvent("Event", parentId, mainUser, "Plarty City", childStart, childEnd, parentTwoDays);
        RecurringEvent child2 = new RecurringEvent("Event", parentId, mainUser, "Plarty City", childStart2, childEnd2, parentTwoDays);
        RecurringEvent child3 = new RecurringEvent("Event", parentId, mainUser, "Plarty City", childStart.plusWeeks(1), childEnd.plusWeeks(1), parentTwoDays);
        RecurringEvent child4 = new RecurringEvent("Event", parentId, mainUser, "Plarty City", childStart2.plusWeeks(1), childEnd2.plusWeeks(1), parentTwoDays);
        RecurringEvent child5 = new RecurringEvent("Event", parentId, mainUser, "Plarty City", childStart.plusWeeks(2), childEnd.plusWeeks(2), parentTwoDays);
        RecurringEvent child6 = new RecurringEvent("Event", parentId, mainUser, "Plarty City", childStart2.plusWeeks(2), childEnd2.plusWeeks(2), parentTwoDays);

        ArrayList<RecurringEvent> childrenGeneral = new ArrayList<>();

        childrenGeneral.add(child1);
        childrenGeneral.add(child2);
        childrenGeneral.add(child3);
        childrenGeneral.add(child4);
        childrenGeneral.add(child5);
        childrenGeneral.add(child6);
        ArrayList<RecurringEvent> childrenGeneratedTwoDays = generateRecurringEvents.generateEvents(parentTest, start, end.plusWeeks(3));

        //six children should be generated on 9/5, 9/7, 9/12, 9/14, 9/19, and 9/21
        assertEquals(6, childrenGeneratedTwoDays.size());
        assertEquals(childrenGeneral, childrenGeneratedTwoDays);

        System.out.println("\n\t- Test getAlteration with two day recurrence");
        DateTime key1 = childStart.withTimeAtStartOfDay();

        Interval newInterval = new Interval(childStart.plusHours(1), childEnd.plusHours(1));

        parentTest.createAlteration(key1, newInterval);

        assertNotNull(accessRecurringEvents.getAlteration(parentTest, key1));
        assertEquals(newInterval, accessRecurringEvents.getAlteration(parentTest, key1));

        System.out.println("\n\t- Test createAlteration with two day recurrence");
        DateTime key2 = child2.getStart().withTimeAtStartOfDay();
        accessRecurringEvents.createAlteration(parentTest, child2.getStart(), newInterval);

        assertNotNull(accessRecurringEvents.getAlteration(parentTest, key2));
        assertEquals(newInterval, accessRecurringEvents.getAlteration(parentTest, key2));

        System.out.println("\n\t- Test deleteDay with two day recurrence");
        DateTime key3 = child3.getStart().withTimeAtStartOfDay();
        accessRecurringEvents.deleteDay(parentTest, child3.getStart());
        childrenGeneratedTwoDays = generateRecurringEvents.generateEvents(parentTwoDays, start, end);

        assertNull(accessRecurringEvents.getAlteration(parentTwoDays, key3));
        for (RecurringEvent child : childrenGeneratedTwoDays) {
            assertNotEquals(key3, child.getStart().withTimeAtStartOfDay());
        }

        System.out.println("\n\t- Test clearAlterations with two day recurrence");
        accessRecurringEvents.clearAlterations(parentTest);

        assertNull(accessRecurringEvents.getAlteration(parentTest, key1));
        assertNull(accessRecurringEvents.getAlteration(parentTest, key2));
        assertNull(accessRecurringEvents.getAlteration(parentTest, key3));

        System.out.println("\n\t- Test setRecurringStartTime with two day recurrence");
        DateTime newStartTime = parentTest.getStart().minusHours(1);
        accessRecurringEvents.setRecurringStartTime(parentTest, newStartTime);
        //we additionally add one alteration to make sure it isn't affected
        accessRecurringEvents.createAlteration(parentTest, child1.getStart().withTimeAtStartOfDay(), newInterval);
        childrenGeneratedTwoDays = generateRecurringEvents.generateEvents(parentTest, start, end.plusWeeks(3));

        assertEquals(11, parentTest.getStart().getHourOfDay());
        for (RecurringEvent child : childrenGeneratedTwoDays) {
            if (accessRecurringEvents.getAlteration(parentTest, child.getStart().withTimeAtStartOfDay()) == null) {
                assertEquals(11, child.getStart().getHourOfDay());
            } else {
                assertNotEquals(11, child.getStart().getHourOfDay());
            }
        }

        System.out.println("\n\t- Test setRecurringEndTime with two day recurrence");
        DateTime newEndTime = parentTest.getEnd().minusHours(1);
        accessRecurringEvents.setRecurringEndTime(parentTest, newEndTime);
        childrenGeneratedTwoDays = generateRecurringEvents.generateEvents(parentTest, start, end.plusWeeks(3));

        assertEquals(12, parentTest.getEnd().getHourOfDay());
        assertEquals(30, parentTest.getEnd().getMinuteOfHour());
        for (RecurringEvent child : childrenGeneratedTwoDays) {
            if (accessRecurringEvents.getAlteration(parentTest, child.getStart().withTimeAtStartOfDay()) == null) {
                assertEquals(12, child.getEnd().getHourOfDay());
            } else {
                assertNotEquals(12, child.getEnd().getHourOfDay());
            }
            assertEquals(30, child.getEnd().getMinuteOfHour());
        }

        System.out.println("\n- Completed Testing: twoDayRecurrence");
    }

    @Test
    public void testOneDayRecurrence() {
        System.out.println("\n- Testing: oneDayRecurrence");
        //setup
        DateTime start = new DateTime(2022, 9, 4, 7, 15);
        DateTime end = new DateTime(2022, 9, 4, 8, 0);

        DateTime childStart = new DateTime(2022, 9, 4, 12, 0);
        DateTime childEnd = new DateTime(2022, 9, 4, 13, 30);

        DateTime expectedStartTime = new DateTime(2022, 9, 6, 7, 15);
        DateTime expectedEndTime = new DateTime(2022, 9, 6, 8, 0);

        HashSet<Integer> oneDay = new HashSet<>();

        oneDay.add(DateTimeConstants.TUESDAY);

        System.out.println("\n\t- Test createEvent on one day recurrence");
        System.out.println(accessUsers.getAttendingEvents(mainUser));
        RecurringEventGenerator oneDayTest = (RecurringEventGenerator) accessRecurringEvents.createRecurringEvent("3350con", mainUser, "E2-330", start, end, oneDay, accessUsers.getAttendingEvents(mainUser));
        int parentId = oneDayTest.getId();

        RecurringEventGenerator parentOneDay = new RecurringEventGenerator("3350con", parentId, mainUser, "E2-330", start, end, oneDay);
        assertEquals(parentOneDay, oneDayTest);

        parentId = parentOneDay.getId();

        System.out.println("\n\t- Test getEvent on one day recurrence");
        assertEquals(parentOneDay, accessRecurringEvents.getEvent(parentId));

        System.out.println("\n\t- Test generateEvents on one day recurrence");
        ArrayList<RecurringEvent> childrenOneDay = new ArrayList<>();
        childrenOneDay.add(new RecurringEvent("3350con", parentId, mainUser, "E2-330",
                expectedStartTime, expectedEndTime, parentOneDay));
        childrenOneDay.add(new RecurringEvent("3350con", parentId, mainUser, "E2-330",
                expectedStartTime.plusDays(7), expectedEndTime.plusDays(7), parentOneDay));
        ArrayList<RecurringEvent> childrenGeneratedOneDay = generateRecurringEvents.generateEvents(parentOneDay, start, end.plusWeeks(2));

        //two children should be generated on 9/6 and 9/13
        System.out.println("actual: " + childrenOneDay);
        System.out.println("generated: " + childrenGeneratedOneDay);
        assertEquals(2, childrenGeneratedOneDay.size());
        for (RecurringEvent child : childrenGeneratedOneDay) {
            System.out.println(child);
            assertTrue(childrenOneDay.contains(child));
        }

        System.out.println("\n\t- Test getAlteration on one day recurrence");
        Interval newInterval = new Interval(childStart.plusHours(2), childEnd.plusHours(2));
        DateTime key1 = childStart.withTimeAtStartOfDay();
        parentOneDay.createAlteration(key1, newInterval);

        assertNotNull(accessRecurringEvents.getAlteration(parentOneDay, key1));
        assertEquals(newInterval, accessRecurringEvents.getAlteration(parentOneDay, key1));

        System.out.println("\n\t- Test createAlteration on one day recurrence");
        DateTime key2 = childStart.plusWeeks(1).withTimeAtStartOfDay();
        accessRecurringEvents.createAlteration(parentOneDay, key2, newInterval);

        assertNotNull(accessRecurringEvents.getAlteration(parentOneDay, key2));
        assertEquals(newInterval, accessRecurringEvents.getAlteration(parentOneDay, key2));

        System.out.println("\n\t- Test deleteDay on  one day recurrence");
        accessRecurringEvents.deleteDay(parentOneDay, key2);

        assertNull(accessRecurringEvents.getAlteration(parentOneDay, key2));
        for (RecurringEvent child : childrenGeneratedOneDay) {
            assertNotEquals(key2, child.getStart().withTimeAtStartOfDay());
        }

        System.out.println("\n\t- Test clearAlterations on one day recurrence");
        accessRecurringEvents.clearAlterations(parentOneDay);

        assertNull(accessRecurringEvents.getAlteration(parentOneDay, key1));
        assertNull(accessRecurringEvents.getAlteration(parentOneDay, key2));

        System.out.println("\n\t- Test Check setRecurringStartTime on one day recurrence");
        DateTime newStartTime = parentOneDay.getStart().minusHours(1);
        accessRecurringEvents.setRecurringStartTime(parentOneDay, newStartTime);
        accessRecurringEvents.createAlteration(parentOneDay, key2, newInterval);
        childrenGeneratedOneDay = generateRecurringEvents.generateEvents(parentOneDay, start, end.plusWeeks(1));

        assertEquals(6, parentOneDay.getStart().getHourOfDay());
        for (RecurringEvent child : childrenGeneratedOneDay) {
            if (accessRecurringEvents.getAlteration(parentOneDay, child.getStart().withTimeAtStartOfDay()) == null) {
                assertEquals(6, child.getStart().getHourOfDay());
            } else {
                assertNotEquals(6, child.getStart().getHourOfDay());
            }
        }

        System.out.println("\n\t- Test Check setRecurringEndTime on one day recurrence");
        DateTime newEndTime = parentOneDay.getEnd().minusHours(1);
        accessRecurringEvents.setRecurringEndTime(parentOneDay, newEndTime);
        childrenGeneratedOneDay = generateRecurringEvents.generateEvents(parentOneDay, start, end.plusWeeks(1));

        assertEquals(7, parentOneDay.getEnd().getHourOfDay());
        assertEquals(0, parentOneDay.getEnd().getMinuteOfHour());
        for (RecurringEvent child : childrenGeneratedOneDay) {
            if (accessRecurringEvents.getAlteration(parentOneDay, child.getEnd().withTimeAtStartOfDay()) == null) {
                assertEquals(7, child.getEnd().getHourOfDay());
            } else {
                assertNotEquals(7, child.getEnd().getHourOfDay());
            }
            assertEquals(0, child.getEnd().getMinuteOfHour());
        }

        System.out.println("\n\t- Delete the event one day recurrence");
        assertTrue(accessRecurringEvents.deleteEvent(parentId));
        assertNull(accessRecurringEvents.getEvent(parentId));

        System.out.println("\n- Completed Testing: oneDayRecurrence");
    }


    @Test
    public void testAllDayRecurrence() {
        System.out.println("\n- Testing: allDaysRecurrence");

        //setup
        DateTime start = new DateTime(2022, 10, 10, 5, 15);
        DateTime end = new DateTime(2022, 10, 10, 5, 30);

        DateTime childStart = new DateTime(2022, 11, 14, 12, 0);
        DateTime childEnd = new DateTime(2022, 12, 18, 13, 30);

        HashSet<Integer> allDays = new HashSet<>();

        allDays.add(DateTimeConstants.MONDAY);
        allDays.add(DateTimeConstants.TUESDAY);
        allDays.add(DateTimeConstants.WEDNESDAY);
        allDays.add(DateTimeConstants.THURSDAY);
        allDays.add(DateTimeConstants.FRIDAY);
        allDays.add(DateTimeConstants.SATURDAY);
        allDays.add(DateTimeConstants.SUNDAY);

        System.out.println("\n\t- Test createEvent on all days");
        RecurringEventGenerator allDaysTest = (RecurringEventGenerator) accessRecurringEvents.createRecurringEvent("Grace Hopper Fanclub meeting", mainUser, "CSSA Lounge", start, end, allDays, accessUsers.getAttendingEvents(mainUser));
        int parentId = allDaysTest.getId();
        RecurringEventGenerator parentAllDays = new RecurringEventGenerator("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start, end, allDays);

        assertEquals(parentAllDays, allDaysTest);

        System.out.println("\n\t- Trying to get an invalid id");
        assertNull(accessRecurringEvents.getEvent(-1));
        assertNull(accessRecurringEvents.getEvent(500));

        System.out.println("\n\t- Test getEvent on all days");
        assertEquals(parentAllDays, accessRecurringEvents.getEvent(parentId));

        System.out.println("\n\t- Test generateEvents on all days, <1 week span");
        ArrayList<RecurringEvent> childrenAllDays = new ArrayList<>();
        childrenAllDays.add(new RecurringEvent("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start, end, parentAllDays));
        childrenAllDays.add(new RecurringEvent("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start.plusDays(1), end.plusDays(1), parentAllDays));
        childrenAllDays.add(new RecurringEvent("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start.plusDays(2), end.plusDays(2), parentAllDays));
        childrenAllDays.add(new RecurringEvent("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start.plusDays(3), end.plusDays(3), parentAllDays));
        childrenAllDays.add(new RecurringEvent("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start.plusDays(4), end.plusDays(4), parentAllDays));

        ArrayList<RecurringEvent> childrenGeneratedAllDays = generateRecurringEvents.generateEvents(parentAllDays, start, end.plusDays(4));

        //five children should be generated on 9/4, 9/5, 9/6, and 9/7
        assertEquals(5, childrenGeneratedAllDays.size());
        assertEquals(childrenAllDays, childrenGeneratedAllDays);

        System.out.println("\n\t- Test generateEvents on all twoDays, >=1 week span");
        //note the offset: start is on 9/4 but childStart is on 9/5, so we have to do some date math for accurate results…

        childrenGeneratedAllDays = generateRecurringEvents.generateEvents(parentAllDays, start, end.plusDays(7));
        childrenAllDays.add(new RecurringEvent("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start.plusDays(5), end.plusDays(5), parentAllDays));
        childrenAllDays.add(new RecurringEvent("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start.plusDays(6), end.plusDays(6), parentAllDays));
        childrenAllDays.add(new RecurringEvent("Grace Hopper Fanclub meeting", parentId, mainUser, "CSSA Lounge", start.plusDays(7), end.plusDays(7), parentAllDays));

        //eight children should be generated on 9/4 through 9/12
        assertEquals(8, childrenGeneratedAllDays.size());
        assertEquals(childrenAllDays, childrenGeneratedAllDays);

        System.out.println("\n\t- Test getAlteration on all twoDays");
        Interval newInterval = new Interval(childStart.plusMinutes(20), childEnd.plusMinutes(20));
        DateTime key1 = childStart.withTimeAtStartOfDay();
        parentAllDays.createAlteration(key1, newInterval);

        assertNotNull(accessRecurringEvents.getAlteration(parentAllDays, key1));
        assertEquals(newInterval, accessRecurringEvents.getAlteration(parentAllDays, key1));

        System.out.println("\n\t- Test createAlteration on all twoDays");
        DateTime key2 = childStart.plusWeeks(1).withTimeAtStartOfDay();
        accessRecurringEvents.createAlteration(parentAllDays, key2, newInterval);

        assertNotNull(accessRecurringEvents.getAlteration(parentAllDays, key2));
        assertEquals(newInterval, accessRecurringEvents.getAlteration(parentAllDays, key2));

        System.out.println("\n\t- Test deleteDay on all twoDays");
        DateTime key3 = childStart.plusDays(3).withTimeAtStartOfDay();
        accessRecurringEvents.deleteDay(parentAllDays, key3);
        childrenGeneratedAllDays = generateRecurringEvents.generateEvents(parentAllDays, start, end.plusWeeks(1));

        assertNull(accessRecurringEvents.getAlteration(parentAllDays, key3));
        for (RecurringEvent child : childrenGeneratedAllDays) {
            assertNotEquals(key3, child.getStart().withTimeAtStartOfDay());
        }

        System.out.println("\n\t- Test clearAlterations on all twoDays");
        accessRecurringEvents.clearAlterations(parentAllDays);

        assertNull(accessRecurringEvents.getAlteration(parentAllDays, key1));
        assertNull(accessRecurringEvents.getAlteration(parentAllDays, key2));
        assertNull(accessRecurringEvents.getAlteration(parentAllDays, key3));

        System.out.println("\n\t- Test setRecurringStartTime on all twoDays");
        DateTime newStartTime = parentAllDays.getStart().minusHours(1);
        accessRecurringEvents.setRecurringStartTime(parentAllDays, newStartTime);
        accessRecurringEvents.createAlteration(parentAllDays, key1, newInterval);
        childrenGeneratedAllDays = generateRecurringEvents.generateEvents(parentAllDays, start, end.plusWeeks(1));

        assertEquals(4, parentAllDays.getStart().getHourOfDay());
        for (RecurringEvent child : childrenGeneratedAllDays) {
            if (accessRecurringEvents.getAlteration(parentAllDays, child.getStart().withTimeAtStartOfDay()) == null) {
                assertEquals(4, child.getStart().getHourOfDay());
            } else {
                assertNotEquals(4, child.getStart().getHourOfDay());
            }
        }

        System.out.println("\n\t- Test setRecurringEndTime on all twoDays");
        DateTime newEndTime = parentAllDays.getEnd().minusHours(1);
        accessRecurringEvents.setRecurringEndTime(parentAllDays, newEndTime);
        childrenGeneratedAllDays = generateRecurringEvents.generateEvents(parentAllDays, start, end.plusWeeks(1));

        assertEquals(4, parentAllDays.getEnd().getHourOfDay());
        for (RecurringEvent child : childrenGeneratedAllDays) {
            if (accessRecurringEvents.getAlteration(parentAllDays, child.getStart().withTimeAtStartOfDay()) == null) {
                assertEquals(4, child.getEnd().getHourOfDay());
            } else {
                assertNotEquals(4, child.getEnd().getHourOfDay());
            }
            assertEquals(30, child.getEnd().getMinuteOfHour());
        }

        System.out.println("\n- Completed Testing: allDaysRecurrence");
    }


    @Test
    public void testGenerateMultipleEvents() {
        System.out.println("\n- Testing: generatingMultipleEvents");
        //setup
        DateTime start = new DateTime(2022, 10, 10, 15, 15);
        DateTime end = new DateTime(2022, 10, 10, 15, 30);

        HashSet<Integer> oneDay = new HashSet<>();
        HashSet<Integer> twoDays = new HashSet<>();

        twoDays.add(DateTimeConstants.SATURDAY);
        twoDays.add(DateTimeConstants.SUNDAY);

        oneDay.add(DateTimeConstants.SUNDAY);

        System.out.println("\n- Creating an user");
        User anUser = accessUsers.createUser("Definitely an user");

        System.out.println("\n- Create all the types of event multipleEvents");
        SingleEvent newEvent = (SingleEvent) accessSingleEvents.createSingleEvent("Class", anUser, "UofM", start.minusDays(1), end.minusDays(1));
        assertEquals(1, accessUsers.getSchedule(anUser, start.minusDays(3), end).size());
        assertEquals(newEvent, accessUsers.getOwnedEvents(anUser).get(0));

        assertTrue(accessUsers.canCreateRecurring(start.plusMinutes(30), end.plusMinutes(30), accessUsers.getSchedule(mainUser, start.plusMinutes(30), end.plusMinutes(30))));
        RecurringEventGenerator parentTwoDays = (RecurringEventGenerator) accessRecurringEvents.createRecurringEvent("Class", anUser, "UofM", start.plusMinutes(30), end.plusMinutes(30), twoDays, accessUsers.getAttendingEvents(anUser));

        assertTrue(accessUsers.canCreateRecurring(start.plusHours(1), end.plusHours(1), accessUsers.getSchedule(mainUser, start.plusHours(1), end.plusHours(1))));
        RecurringEventGenerator parentOneDay = (RecurringEventGenerator) accessRecurringEvents.createRecurringEvent("Party", anUser, "Downtown", start.plusHours(1), end.plusHours(1), oneDay, accessUsers.getAttendingEvents(anUser));

        assertEquals(0, accessUsers.getConflictingEvents(anUser, start, end.plusHours(1)).size());

        System.out.println("\n\t- Generates multiple non-overlapping events");
        ArrayList<RecurringEvent> childrenGeneratedTwoDays = generateRecurringEvents.generateEvents(parentTwoDays, start, start.plusWeeks(1));

        ArrayList<RecurringEvent> childrenGeneratedOneDay = generateRecurringEvents.generateEvents(parentOneDay, start, start.plusWeeks(1));
        System.out.println(childrenGeneratedOneDay);
        assertEquals(1, childrenGeneratedOneDay.size());
        assertEquals(2, childrenGeneratedTwoDays.size());

        System.out.println("\n\t- Check the user schedule is updated");
        assertEquals(4, accessUsers.getSchedule(anUser, start.minusDays(3), end).size());

        System.out.println("\n\t- Setting inactive hours");
        assertTrue(accessRecurringEvents.setActiveHours(anUser, 8, 0, 23, 0));
        assertEquals(8, accessUsers.getSchedule(anUser, start.minusDays(3), end).size());

        System.out.println("\n\t- Creating and event with an  invalid name");
        assertNull(accessSingleEvents.createSingleEvent("", anUser, "UofM", start.minusDays(1), end.minusDays(1)));
        assertNull(accessRecurringEvents.createRecurringEvent(null, anUser, "location", start, end, twoDays, new ArrayList<>()));
        assertNull(accessRecurringEvents.createRecurringEvent("", anUser, "location", start, end, twoDays, new ArrayList<>()));
        assertEquals(8, accessUsers.getSchedule(anUser, start.minusDays(3), end).size());

        System.out.println("\n\t- createEvent on invalid intervals");
        assertNull(accessRecurringEvents.createRecurringEvent("event", anUser, "location", null, end, twoDays, new ArrayList<>()));
        assertNull(accessRecurringEvents.createRecurringEvent("event", anUser, "location", start, null, twoDays, new ArrayList<>()));
        assertNull(accessRecurringEvents.createRecurringEvent("event", anUser, "location", start, start.plusHours(25), twoDays, new ArrayList<>()));
        assertEquals(8, accessUsers.getSchedule(anUser, start.minusDays(3), end).size());

        System.out.println("\n- Completed Testing: generatingMultipleEvents");
    }

}

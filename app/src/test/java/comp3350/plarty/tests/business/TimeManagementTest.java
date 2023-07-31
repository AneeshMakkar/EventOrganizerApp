package comp3350.plarty.tests.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import comp3350.plarty.application.Main;
import comp3350.plarty.application.Services;
import comp3350.plarty.business.AccessSingleEvents;
import comp3350.plarty.business.AccessUsers;
import comp3350.plarty.business.TimeManagement;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;
import comp3350.plarty.persistence.DataAccess;
import comp3350.plarty.persistence.DataAccessStub;


public class TimeManagementTest {
    DataAccess dataAccessStub;
    AccessUsers accessUsers;
    AccessSingleEvents accessSingleEvents;
    TimeManagement timeManagement;
    DateTime start1, end1, start2, end2, start3, end3, start4, end4,start5,end5;
    User userFree, anOrganizer, zeroCool;
    SingleEvent plarty,testEvent,testEvent2;


    @Before
    public void setUp() {
        dataAccessStub= Services.createDataAccess(new DataAccessStub());
        accessUsers = new AccessUsers();
        accessSingleEvents = new AccessSingleEvents();
        
        timeManagement = new TimeManagement();
        start1 = new DateTime(2022, 8, 12, 8, 30);
        end1 = new DateTime(2022, 8, 12, 12, 30);

        start2 = new DateTime(2022, 8, 12, 13, 30);
        end2 = new DateTime(2022, 8, 12, 16, 30);

        start3 = new DateTime(2022, 8, 12, 20, 0);
        end3 = new DateTime(2022, 8, 12, 23, 30);

        start4 = new DateTime(2022, 8, 12, 15, 30);
        end4 = new DateTime(2022, 8, 12, 17, 30);

        start5 = new DateTime(2022,8,12,21,0);
        end5 = new DateTime(2022,8,12,21,30);

        userFree = new User("Im always free", 1);
        anOrganizer = new User("Im just an organizer", 2);
        zeroCool = new User("Zero Cool", 3);

        SingleEvent zeroCoolEvent1 = new SingleEvent("work", 1, anOrganizer, "U of M", start1, end2);
        SingleEvent zeroCoolEvent2 = new SingleEvent("school", 2, anOrganizer, "U of M", start2, end2);

        accessUsers.getAttendingEvents(zeroCool).add(zeroCoolEvent1);
        accessUsers.getAttendingEvents(zeroCool).add(zeroCoolEvent2);

        plarty = new SingleEvent("Plarty", 3, anOrganizer, "U of M", start3, end3);

        accessSingleEvents.getInviteResponses(plarty).put(userFree, InviteResponse.ACCEPTED);
        accessSingleEvents.getInviteResponses(plarty).put(zeroCool, InviteResponse.MAYBE);

        testEvent = new SingleEvent("testEvent", 4, anOrganizer, "U of M", start5, end5);
        accessSingleEvents.getInviteResponses(testEvent).put(userFree, InviteResponse.ACCEPTED);

        testEvent2 = new SingleEvent("testEvent2", 4, anOrganizer, "U of M", start5, end5);
        accessSingleEvents.getInviteResponses(testEvent2).put(userFree, InviteResponse.ACCEPTED);

        dataAccessStub.open(Main.dbName);
    }

    @After
    public void tearDown() {
        dataAccessStub.close();
    }

    @Test
    public void testTypicalValidDate() {
        System.out.println("\n- Testing: TimeManagement - typicalCasesDateValidation");

        start1 = new DateTime(2022, 8, 12, 8, 30);
        end1 = new DateTime(2022, 8, 12, 12, 30);

        start2 = new DateTime(2022, 8, 12, 13, 30);
        end2 = new DateTime(2022, 8, 12, 16, 30);

        assertTrue(timeManagement.validInterval(start1, end1));
        assertTrue(timeManagement.validInterval(start2, end2));
        assertTrue(timeManagement.validInterval(start1, end2));

        System.out.println("\n- Completed Testing: TimeManagement - typicalCasesDateValidation");
    }

    @Test
    public void testEdgeCaseValidDate() {
        System.out.println("\n- Testing: TimeManagement - testEdgeCaseValidDate");

        DateTime dayEnd = new DateTime(2022, 9, 12, 11, 59);
        DateTime dayStart = new DateTime(2022, 10, 12, 0, 0);

        start2 = new DateTime(2022, 8, 12, 13, 30);
        end2 = new DateTime(2022, 8, 12, 16, 30);

        start3 = new DateTime(2022, 8, 12, 20, 0);
        end3 = new DateTime(2022, 8, 12, 23, 30);

        System.out.println("\n- Testing: validDate - start 23:59 end 00 next day");
        assertTrue(timeManagement.validInterval(dayEnd, dayStart));

        System.out.println("\n- Testing: validDate - 24 hour interval");
        assertTrue(timeManagement.validInterval(dayEnd, dayEnd.plusDays(1)));

        System.out.println("\n- Testing: validDate - 24 hour interval + 1 millisecond");
        assertTrue(timeManagement.validInterval(dayEnd, dayEnd.plusDays(1).plusMillis(1)));

        System.out.println("\n- Testing: validDate - 1 millisecond before start");
        assertFalse(timeManagement.validInterval(dayEnd, dayEnd.minusMillis(1)));

        System.out.println("\n- Testing: validDate - start and end in the same time");
        assertFalse(timeManagement.validInterval(start1, start1));
        assertFalse(timeManagement.validInterval(start2, start2));
        assertFalse(timeManagement.validInterval(start3, start3));

        System.out.println("\n- Testing: validDate - end before start");
        assertFalse(timeManagement.validInterval(end1, start1));
        assertFalse(timeManagement.validInterval(end2, start2));
        assertFalse(timeManagement.validInterval(end3, start3));

        System.out.println("\n- Completed Testing: TimeManagement - testEdgeCaseValidDate");
    }

    @Test
    public void testTypicalGetSuggestedTimeList() {
        System.out.println("\n- Testing: TimeManagement - typicalGetSuggestedTimeList");

        ArrayList<Interval> listOfSuggestedTime = new ArrayList<>();

        listOfSuggestedTime.add(new Interval(start3, start3.plusMinutes(30)));
        listOfSuggestedTime.add(new Interval(start3.plusMinutes(30), start3.plusMinutes(60)));
        listOfSuggestedTime.add(new Interval(start3.plusMinutes(60), start3.plusMinutes(90)));
        assertEquals(listOfSuggestedTime,
                timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(plarty), start3, end3, 30));

        listOfSuggestedTime.clear();
        listOfSuggestedTime.add(new Interval(start3, start3.plusMinutes(15)));
        listOfSuggestedTime.add(new Interval(start3.plusMinutes(15), start3.plusMinutes(30)));
        listOfSuggestedTime.add(new Interval(start3.plusMinutes(30), start3.plusMinutes(45)));
        assertEquals(listOfSuggestedTime,
                timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(plarty), start3, end3, 15));

        System.out.println("\n- Completed Testing: TimeManagement - typicalGetSuggestedTimeList");
    }


    @Test
    public void testSuggestedTimeListEdgeCases(){
        System.out.println("\n- Testing: TimeManagement - testSuggestedTimeEdgeCases");

        System.out.println("\n- Testing: GetSuggestedTimeList- event between another event time");
        ArrayList<Interval> listOfSuggestedTime=new ArrayList<>();

        listOfSuggestedTime.add(new Interval(start3,start3.plusMinutes(30)));
        listOfSuggestedTime.add(new Interval(start3.plusMinutes(30),start3.plusMinutes(60)));
        listOfSuggestedTime.add(new Interval(start3.plusMinutes(60),start3.plusMinutes(90)));
        assertEquals(listOfSuggestedTime,
                timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(plarty), start3, end3, 30));

        System.out.println("\n- Testing: GetSuggestedTimeList- No time Suggested");
        listOfSuggestedTime.clear();
        assertEquals(listOfSuggestedTime,
                timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(testEvent2), start5, end5, 30));
        System.out.println("\n- Completed Testing: TimeManagement - testSuggestedTimeEdgeCases");
    }

    @Test
    public void testInvalidCases() {
        System.out.println("\n- Testing: TimeManagement - testInvalidCases");

        System.out.println("\n- Testing: validDate - Invalid times");
        assertFalse(timeManagement.validInterval(start1, start1));
        assertFalse(timeManagement.validInterval(end1, start1));
        assertTrue(timeManagement.validInterval(start1.minusHours(3), end1));

        System.out.println("\n- Testing: validDate - Null input");
        assertFalse(timeManagement.validInterval(null, end1));
        assertFalse(timeManagement.validInterval(start1, null));
        assertFalse(timeManagement.validInterval(null, null));

        System.out.println("\n\t- Test generateEvents - Invalid Times");
        assertTrue(timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(plarty), end3, start3, 0).isEmpty());
        assertTrue(timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(plarty), start3, end3, -100).isEmpty());
        assertTrue(timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(plarty), start3, end3, 0).isEmpty());

        System.out.println("\n\t- Test generateEvents - NullInput");
        assertTrue(timeManagement.getSuggestedTimeList(null, start3, end3, 15).isEmpty());
        assertTrue(timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(plarty), null, end3, 15).isEmpty());
        assertTrue(timeManagement.getSuggestedTimeList(accessSingleEvents.getInvitees(plarty), start3, null, 15).isEmpty());

        System.out.println("\n- Completed Testing: TimeManagement - testInvalidCases");
    }

}

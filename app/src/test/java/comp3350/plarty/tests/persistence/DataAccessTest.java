package comp3350.plarty.tests.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.RecurringEventGenerator;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;
import comp3350.plarty.persistence.DataAccess;
import comp3350.plarty.persistence.DataAccessStub;

public class DataAccessTest {
    private DataAccess dataAccess;

    @Before
    public void setUp() {
        System.out.println("\nStarting Persistence test DataAccess (using stub)");
        dataAccess = new DataAccessStub();
        dataAccess.open("Stub");
    }

    @After
    public void tearDown() {
        dataAccess.close();
    }

    @Test
    public void testMainUserCreatesAParty() {
        System.out.println("\n- Testing: DataAccessTest - Main user creates a party");

        System.out.println("\n\t- Creating an User so it can attend to the party");
        User user = dataAccess.createUser("McArthur");
        assertEquals("McArthur", user.getName());

        System.out.println("\n\t- User decides to change their name");
        user = new User("NoName", user.getId());
        assertTrue(dataAccess.updateUser(user));
        assertEquals("NoName", dataAccess.getUser(user.getId()).getName());

        System.out.println("\n\t- Testing the getter");
        User userFromGetter = dataAccess.getUser(user.getId());
        assertEquals(user.getName(), userFromGetter.getName());
        assertEquals(user.getId(), userFromGetter.getId());

        System.out.println("\n\t- Checking the main user is the correct one");
        User mainUser = dataAccess.getMainUser();
        assertEquals(mainUser.getName(), "John Braico");
        assertEquals(mainUser.getId(), 0);

        System.out.println("\n\t- Main user creates a single event");
        DateTime startDay = new DateTime(2022, 10, 1, 10, 30);
        Event singleEvent = new SingleEvent("Home Party", dataAccess.assignEventId(), mainUser,
                "Home", startDay, startDay.plusHours(2));
        assertTrue(dataAccess.createEvent(singleEvent));

        System.out.println("\n\t- Testing the getter of the single event");
        Event eventFromGetter = dataAccess.getEvent(singleEvent.getId());
        assertEquals(singleEvent.getId(), eventFromGetter.getId());
        assertEquals(singleEvent.getStart(), eventFromGetter.getStart());
        assertEquals(singleEvent.getEnd(), eventFromGetter.getEnd());
        assertEquals(singleEvent.getLocation(), eventFromGetter.getLocation());
        assertEquals(singleEvent.getInterval(), eventFromGetter.getInterval());
        assertEquals(singleEvent.getName(), eventFromGetter.getName());
        assertEquals(singleEvent.getOrganiserId(), eventFromGetter.getOrganiserId());

        System.out.println("\n\t- Main user invites a friend to his party");
        assertTrue(dataAccess.createInvite(singleEvent.getId(), user.getId()));

        System.out.println("\n\t- User invited is not sure if they want to go to the party");
        assertTrue(dataAccess.updateInvite(singleEvent.getId(), user.getId(), InviteResponse.MAYBE));
        assertEquals(InviteResponse.MAYBE, dataAccess.getInvite(singleEvent.getId(), user.getId()));

        System.out.println("\n\t- User decided to accept the invitation");
        assertTrue(dataAccess.updateInvite(singleEvent.getId(), user.getId(), InviteResponse.ACCEPTED));
        assertEquals(InviteResponse.ACCEPTED, dataAccess.getInvite(singleEvent.getId(), user.getId()));

        System.out.println("\n\t- User had some family stuff come up and they cant make it");
        assertTrue(dataAccess.updateInvite(singleEvent.getId(), user.getId(), InviteResponse.DECLINED));
        assertEquals(InviteResponse.DECLINED, dataAccess.getInvite(singleEvent.getId(), user.getId()));

        System.out.println("\n\t- Since the user declined it deletes the invite");
        assertTrue(dataAccess.deleteInvite(singleEvent.getId(), user.getId()));

        System.out.println("\n\t- Main user changes the Location ");

        singleEvent.setLocation("Plarty Club");
        assertTrue(dataAccess.updateEvent(singleEvent));
        assertEquals(singleEvent.getLocation(), dataAccess.getEvent(singleEvent.getId()).getLocation());

        System.out.println("\n- Completed Testing: DataAccessTest ");
    }

    @Test
    public void testUserSetsARecurringEvent() {
        System.out.println("\n- Testing: DataAccessTest - Main user sets a recurring event");

        HashSet<Integer> days = new HashSet<>();
        days.add(DateTimeConstants.MONDAY);
        days.add(DateTimeConstants.TUESDAY);
        days.add(DateTimeConstants.WEDNESDAY);
        days.add(DateTimeConstants.THURSDAY);
        days.add(DateTimeConstants.FRIDAY);
        days.add(DateTimeConstants.SATURDAY);
        days.add(DateTimeConstants.SUNDAY);

        DateTime startDay = new DateTime(2022, 10, 8, 10, 30);
        Event RecurringEvent = new RecurringEventGenerator("Home party", 216, dataAccess.getMainUser(),
                "home", startDay, startDay.plusHours(5), days);

        System.out.println("\n\t- Main user creates a Recurrent event");
        assertTrue(dataAccess.createEvent(RecurringEvent));

        System.out.println("\n\t- Main user creates inactive hours");
        assertTrue(dataAccess.createInactive(RecurringEvent.getId(), 0));

        System.out.println("\n\t- Main user gets to see their inactive hours");
        assertEquals(RecurringEvent, dataAccess.getInactive(0));

        System.out.println("\n- Completed Testing: DataAccessTest - Main user sets a recurring event");
    }

    public static void dataAccessTest(DataAccess dataAccess) {
        DataAccessTest dataAccessTest = new DataAccessTest();
        dataAccessTest.dataAccess = dataAccess;
        dataAccessTest.testMainUserCreatesAParty();
        dataAccessTest.testUserSetsARecurringEvent();
    }


    @Test
    public void testInvalidCases(){
        System.out.println("\n- Testing: testInvalidCases");

        ArrayList<User> testList= dataAccess.getAllOtherUsers(0);
        int maxID=testList.size()-1;

        System.out.println("\n\t- Testing: deleteEvent - deleting null events");
        assertFalse(dataAccess.deleteEvent(maxID+1000));
        assertFalse(dataAccess.deleteEvent(maxID+1500));

        System.out.println("\n\t- Testing: getEvent - get null events");
        assertNull(dataAccess.getEvent(1000));
        assertNull(dataAccess.getEvent(1500));

        System.out.println("\n\t- Testing: getOwnedEvents - get Owned event for negative user id");
        ArrayList<Event> test1= dataAccess.getOwnedEvents(-1);
        assertEquals(0, test1.size());

        ArrayList<Event> test2= dataAccess.getOwnedEvents(-12);
        assertEquals(0, test2.size());

        System.out.println("\n\t- Testing: getOwnedEvents - get Owned event for invalid user id");
        ArrayList<Event> test3 = dataAccess.getOwnedEvents(maxID+1000);
        assertEquals(0, test3.size());

        ArrayList<Event> test4 = dataAccess.getOwnedEvents(maxID+1500);
        assertEquals(0, test4.size());

        System.out.println("\n\t- Testing: getInvitations - get Invitations for event of negative user id");
        HashMap<Event, InviteResponse> testInvite1 = dataAccess.getInvitations(-1);
        assertEquals(0, testInvite1.size());

        HashMap<Event, InviteResponse> testInvite2 = dataAccess.getInvitations(-12);
        assertEquals(0, testInvite2.size());

        System.out.println("\n\t- Testing: getInvitations - get Invitations for event of invalid user id");
        HashMap<Event, InviteResponse> testInvite3 = dataAccess.getInvitations(maxID+1000);
        assertEquals(0, testInvite3.size());

        HashMap<Event, InviteResponse> testInvite4 = dataAccess.getInvitations(maxID+1500);
        assertEquals(0, testInvite4.size());

        System.out.println("\n\t- Testing: getInvitedUsers - get Invited Users for invalid events");
        DateTime start1= new DateTime(2022, 8, 12, 8, 30);
        DateTime end1 = new DateTime(2022, 8, 12, 12, 30);

        SingleEvent testEvent1=new SingleEvent("test event",-1,dataAccess.getUser(1),"test location",start1,end1);
        SingleEvent testEvent2=new SingleEvent("test event",maxID+1500,dataAccess.getUser(1),"test location",start1,end1);
        HashMap<User,InviteResponse> testInviteUser1 = dataAccess.getInvitedUsers(testEvent1);
        HashMap<User,InviteResponse> testInviteUser2 = dataAccess.getInvitedUsers(testEvent2);

        assertEquals(0, testInviteUser1.size());
        assertEquals(0, testInviteUser2.size());

        System.out.println("\n\t- Testing: getUser - get Users for negative id");
        User testGetUser1 = dataAccess.getUser(-1);
        User testGetUser2 = dataAccess.getUser(-100);

        assertNull(testGetUser1);
        assertNull(testGetUser2);

        System.out.println("\n\t- Testing: getUser - get Users for invalid id");
        User testGetUser3 = dataAccess.getUser(maxID+1000);
        User testGetUser4 = dataAccess.getUser(maxID+1500);

        assertNull(testGetUser3);
        assertNull(testGetUser4);

        System.out.println("\n\t- Testing: getAllOtherUsers - get All other Users for negative id");
        ArrayList<User> testGetAllOtherUsers = dataAccess.getAllOtherUsers(0);

        ArrayList<User> testGetAllOtherUsers1 = dataAccess.getAllOtherUsers(-1);
        ArrayList<User> testGetAllOtherUsers2 = dataAccess.getAllOtherUsers(-10);

        assertEquals(testGetAllOtherUsers.size(), testGetAllOtherUsers1.size());
        assertEquals(testGetAllOtherUsers.size(), testGetAllOtherUsers2.size());

        System.out.println("\n\t- Testing: getAllOtherUsers - get All other Users for invalid id");
        testGetAllOtherUsers1 = dataAccess.getAllOtherUsers(maxID+1000);
        testGetAllOtherUsers2 = dataAccess.getAllOtherUsers(maxID+1500);

        assertEquals(testGetAllOtherUsers.size(), testGetAllOtherUsers1.size());
        assertEquals(testGetAllOtherUsers.size(), testGetAllOtherUsers2.size());

        System.out.println("\n\t- Testing: updateUser - Update Users with negative or invalid id");
        assertFalse(dataAccess.updateUser(new User("test", -1)));
        assertFalse(dataAccess.updateUser(new User("test", -10)));

        assertFalse(dataAccess.updateUser(new User("test", maxID+1000)));
        assertFalse(dataAccess.updateUser(new User("test", maxID+1500)));

        System.out.println("\n\t- Testing: createInvite - create Invite for users with negative or invalid id");
        assertFalse(dataAccess.createInvite(1,-1));
        assertFalse(dataAccess.createInvite(1,-10));

        assertFalse(dataAccess.createInvite(1,maxID+1000));
        assertFalse(dataAccess.createInvite(1,maxID+1500));

        System.out.println("\n\t- Testing: createInvite - create Invite for event with negative or invalid id");
        assertFalse(dataAccess.createInvite(-1,1));
        assertFalse(dataAccess.createInvite(-10,1));

        assertFalse(dataAccess.createInvite(maxID+1000,1));
        assertFalse(dataAccess.createInvite(maxID+1500,1));

        System.out.println("\n\t- Testing: getInvite - get Invite response for users with negative or invalid id");
        assertNull(dataAccess.getInvite(1,-1));
        assertNull(dataAccess.getInvite(1,-10));

        assertNull(dataAccess.getInvite(1,maxID+1000));
        assertNull(dataAccess.getInvite(1,maxID+1500));

        System.out.println("\n\t- Testing: getInvite - get Invite response for event with negative or invalid id");
        assertNull(dataAccess.getInvite(-1,1));
        assertNull(dataAccess.getInvite(-10,1));

        assertNull(dataAccess.getInvite(maxID+1000,1));
        assertNull(dataAccess.getInvite(maxID+1500,1));

        System.out.println("\n\t- Testing: deleteInvite - delete Invite for users with negative or invalid id");
        assertFalse(dataAccess.deleteInvite(1,-1));
        assertFalse(dataAccess.deleteInvite(1,-10));

        assertFalse(dataAccess.deleteInvite(1,maxID+1000));
        assertFalse(dataAccess.deleteInvite(1,maxID+1500));

        System.out.println("\n\t- Testing: deleteInvite - delete Invite for event with negative or invalid id");
        assertFalse(dataAccess.deleteInvite(-1,1));
        assertFalse(dataAccess.deleteInvite(-10,1));

        assertFalse(dataAccess.deleteInvite(maxID+1000,1));
        assertFalse(dataAccess.createInvite(maxID+1500,1));

        System.out.println("\n\t- Testing: createInactive - create inactive events for users with negative or invalid id");
        assertFalse(dataAccess.createInactive(1,-1));
        assertFalse(dataAccess.createInactive(1,-10));

        assertFalse(dataAccess.createInactive(1,maxID+1000));
        assertFalse(dataAccess.createInactive(1,maxID+1500));

        System.out.println("\n\t- Testing: createInactive -  create inactive events for event with negative or invalid id");
        assertFalse(dataAccess.createInactive(-1,1));
        assertFalse(dataAccess.createInactive(-10,1));

        assertFalse(dataAccess.createInactive(maxID+1000,1));
        assertFalse(dataAccess.createInactive(maxID+1500,1));

        System.out.println("\n\t- Testing: getInactive -  get inactive events for event with negative or invalid id");

        assertNull(dataAccess.getInactive(-1));
        assertNull(dataAccess.getInactive(-10));

        assertNull(dataAccess.getInactive(maxID+1000));
        assertNull(dataAccess.getInactive(maxID+1500));

        System.out.println("\n- Complete Testing: testInvalidCases");


    }
}

package comp3350.plarty.tests.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

import comp3350.plarty.application.Main;
import comp3350.plarty.objects.Event;
import comp3350.plarty.objects.InviteResponse;
import comp3350.plarty.objects.SingleEvent;
import comp3350.plarty.objects.User;
import comp3350.plarty.persistence.DataAccessStub;

public class DataStubTests {
    DataAccessStub stub;

    @Before
    public void setUp() {
        stub = new DataAccessStub();
        stub.open(Main.dbName);
    }

    @After
    public void tearDown() {
        stub.close();
    }

    @Test
    public void testGetMainUser() {
        User mainUser = stub.getMainUser();
        assertNotNull(mainUser);
        assertEquals(mainUser.getName(), "John Braico");
        assertEquals(mainUser.getId(), 0);
    }

    @Test
    public void testCreateEvent() {
        System.out.println("\n- Testing: Creation of Event");

        DateTime start = new DateTime(2022, 10, 8, 10, 30);
        DateTime end = new DateTime(2022, 10, 8, 11, 30);
        User testUser = new User("Test User", 200);
        Event testEvent = new SingleEvent("Test Event", 200, testUser, "Test Location", start, end);
        boolean result = stub.createEvent(testEvent);
        assertTrue(result);

        System.out.println("\n- Complete Testing:  Creation of Event finished");
    }

    @Test
    public void testCreateUser() {

        System.out.println("Testing Creation of User");

        User testUser = stub.createUser("Test User ");
        assertEquals(testUser.getName(), "Test User ");

        System.out.println("\n- Testing: Creation of User Passed");

        System.out.println("\n- Testing: Id increment test.");

        User testUser2 = stub.createUser("Test User 2");
        assertEquals(testUser2.getName(), "Test User 2");

        assertEquals(testUser2.getId(), testUser.getId() + 1);

        System.out.println("\n- Completed Testing: Id Increment test passed");

    }

    @Test
    public void testDeleteEvent() {
        System.out.println("\n- Testing: deleting event that exists.");

        Event testEvent = stub.getEvent(0);
        assertNotNull(testEvent);

        assertNotNull(stub.getEvent(0));

        assertTrue(stub.deleteEvent(0));

        assertNull(stub.getEvent(0));

        System.out.println("\n- Testing: deleting event that does not exists.");

        assertFalse(stub.deleteEvent(0));

    }


    @Test
    public void testGetEvent() {
        System.out.println("\n- Testing: the retrieval of event that exists.");

        Event testEvent = stub.getEvent(0);

        assertEquals(testEvent.getId(), 0);
        assertEquals(testEvent.getName(), "Surprise Party");
        assertEquals(testEvent.getLocation(), "Milwaukee");

        System.out.println("\n- Testing: retrieval of event that does not exists.");

        testEvent = stub.getEvent(2100);

        assertNull(testEvent);


    }

    @Test
    public void testGetUser() {
        System.out.println("\n- Testing: the retrieval of User that exists.");

        User testUser = stub.getUser(0);

        assertEquals(testUser.getId(), 0);
        assertEquals(testUser.getName(), "John Braico");

        System.out.println("\n- Testing: retrieval of User that does not exists.");

        testUser = stub.getUser(2100);

        assertNull(testUser);

    }

    @Test
    public void testUpdateUser() {
        System.out.println("\n- Testing: update the user with valid id");

        User testUser = new User("test", 0);
        boolean result = stub.updateUser(testUser);
        assertTrue(result);
        assertEquals(testUser.getName(), stub.getMainUser().getName());

        System.out.println("\n- Testing: update the user with invalid id");

        testUser = new User("test", 129);

        result = stub.updateUser(testUser);
        assertFalse(result);


    }

    @Test
    public void testUpdateEvent() {
        System.out.println("\n- Testing: update the Event with valid id");

        User testUser = new User("test", 0);
        DateTime start = new DateTime(2022, 10, 8, 10, 30);
        DateTime end = new DateTime(2022, 10, 8, 11, 30);
        Event testEvent = new SingleEvent("Test Event", 2, testUser, "Test Location", start, end);
        boolean result = stub.updateEvent(testEvent);
        assertTrue(result);
        assertEquals(testEvent.getName(), stub.getEvent(2).getName());

        System.out.println("\n- Testing: update the Event with invalid id");

        testEvent = new SingleEvent("Test Event", 210, testUser, "Test Location", start, end);

        result = stub.updateEvent(testEvent);
        assertFalse(result);


    }

    @Test
    public void testGetOwnedEvents() {

        System.out.println("\n- Testing: getting owned events of the user");
        ArrayList<Event> listOfEvents = stub.getOwnedEvents(0);
        assertEquals(4, listOfEvents.size());
        assertEquals("Surprise Party", listOfEvents.get(0).getName());
        assertEquals(0, listOfEvents.get(0).getId());

        System.out.println("\n- Testing: getting owned events of the user after deleting the event");
        boolean result = stub.deleteEvent(0);
        assertTrue(result);

        listOfEvents = stub.getOwnedEvents(0);
        assertNotEquals("Surprise Party", listOfEvents.get(0).getName());
        assertNotEquals(0, listOfEvents.get(0).getId());


    }

    @Test
    public void testCreateInvite() {

        System.out.println("\n- Testing: invite is created and is in the list of user");

        User testUser = stub.getUser(10);
        Event testEvent = stub.getEvent(0);

        HashMap<User, InviteResponse> listOfUsers = stub.getInvitedUsers((SingleEvent) testEvent);
        assertFalse(listOfUsers.containsKey(testUser));
        boolean result = stub.createInvite(testEvent.getId(), testUser.getId());
        assertTrue(result);
        listOfUsers = stub.getInvitedUsers((SingleEvent) testEvent);
        assertTrue(listOfUsers.containsKey(testUser));


        result = stub.createInvite(12, testUser.getId());
        assertTrue(result);
        listOfUsers = stub.getInvitedUsers((SingleEvent) stub.getEvent(12));
        assertTrue(listOfUsers.containsKey(testUser));

        result = stub.createInvite(13, testUser.getId());
        assertTrue(result);
        listOfUsers = stub.getInvitedUsers((SingleEvent) stub.getEvent(13));
        assertTrue(listOfUsers.containsKey(testUser));

    }

    @Test
    public void testUpdateInvite() {

        System.out.println("\n- Testing: updating the invite response of the user");

        User testUser = stub.getUser(10);
        Event testEvent = stub.getEvent(0);
        boolean result = stub.createInvite(testEvent.getId(), testUser.getId());
        assertTrue(result);
        HashMap<User, InviteResponse> listOfUsers = stub.getInvitedUsers((SingleEvent) testEvent);
        assertTrue(listOfUsers.containsKey(testUser));
        assertEquals(listOfUsers.get(testUser), InviteResponse.NO_RESPONSE);

        result = stub.updateInvite(testEvent.getId(), testUser.getId(), InviteResponse.ACCEPTED);
        assertTrue(result);
        listOfUsers = stub.getInvitedUsers((SingleEvent) testEvent);
        assertEquals(listOfUsers.get(testUser), InviteResponse.ACCEPTED);

        result = stub.updateInvite(testEvent.getId(), testUser.getId(), InviteResponse.DECLINED);
        assertTrue(result);
        listOfUsers = stub.getInvitedUsers((SingleEvent) testEvent);
        assertEquals(listOfUsers.get(testUser), InviteResponse.DECLINED);

    }


    @Test
    public void testGetInvite() {

        System.out.println("\n- Testing: getting the invite response of the user");

        User testUser = stub.getUser(10);
        Event testEvent = stub.getEvent(0);
        boolean result = stub.createInvite(testEvent.getId(), testUser.getId());
        assertTrue(result);
        HashMap<User, InviteResponse> listOfUsers = stub.getInvitedUsers((SingleEvent) testEvent);
        assertTrue(listOfUsers.containsKey(testUser));
        assertEquals(stub.getInvite(testEvent.getId(), testUser.getId()), InviteResponse.NO_RESPONSE);

        result = stub.updateInvite(testEvent.getId(), testUser.getId(), InviteResponse.ACCEPTED);
        assertTrue(result);
        assertEquals(stub.getInvite(testEvent.getId(), testUser.getId()), InviteResponse.ACCEPTED);

        result = stub.updateInvite(testEvent.getId(), testUser.getId(), InviteResponse.DECLINED);
        assertTrue(result);
        assertEquals(stub.getInvite(testEvent.getId(), testUser.getId()), InviteResponse.DECLINED);

    }

    @Test
    public void testDeleteInvite() {

        System.out.println("\n- Testing: deleting invite from the user invite list");
        Event testEvent = stub.getEvent(2);
        User testUser = stub.getUser(0);

        assertTrue(stub.createInvite(testEvent.getId(), testUser.getId()));
        HashMap<User, InviteResponse> listOfInvites = stub.getInvitedUsers((SingleEvent) testEvent);
        assertEquals(2, listOfInvites.size());

        assertTrue(stub.deleteInvite(testEvent.getId(), testUser.getId()));

    }

    @Test
    public void testGetInvitations() {

        System.out.println("\n- Testing: getting the invitations of the users");

        User testUser = stub.getUser(0);
        HashMap<Event, InviteResponse> listOfInvites = stub.getInvitations(testUser.getId());
        assertEquals(7, listOfInvites.size());

        assertTrue(stub.createInvite(10, 0));

        listOfInvites = stub.getInvitations(testUser.getId());
        assertTrue(listOfInvites.containsKey(stub.getEvent(10)));
        assertEquals(8, listOfInvites.size());

        assertTrue(stub.createInvite(11, 0));

        listOfInvites = stub.getInvitations(testUser.getId());
        assertTrue(listOfInvites.containsKey(stub.getEvent(11)));
        assertEquals(9, listOfInvites.size());
    }

    @Test
    public void testInvalidCases() {
        System.out.println("\n- Testing: testInvalidCases");

        ArrayList<User> testList = stub.getAllOtherUsers(0);
        int maxID = testList.size() - 1;

        System.out.println("\n\t- Testing: deleteEvent - deleting null events");
        assertFalse(stub.deleteEvent(maxID + 1000));
        assertFalse(stub.deleteEvent(maxID + 1500));

        System.out.println("\n\t- Testing: getEvent - get null events");
        assertNull(stub.getEvent(1000));
        assertNull(stub.getEvent(1500));

        System.out.println("\n\t- Testing: getOwnedEvents - get Owned event for negative user id");
        ArrayList<Event> test1 = stub.getOwnedEvents(-1);
        assertEquals(0, test1.size());

        ArrayList<Event> test2 = stub.getOwnedEvents(-12);
        assertEquals(0, test2.size());

        System.out.println("\n\t- Testing: getOwnedEvents - get Owned event for invalid user id");
        ArrayList<Event> test3 = stub.getOwnedEvents(maxID + 1000);
        assertEquals(0, test3.size());

        ArrayList<Event> test4 = stub.getOwnedEvents(maxID + 1500);
        assertEquals(0, test4.size());

        System.out.println("\n\t- Testing: getInvitations - get Invitations for event of negative user id");
        HashMap<Event, InviteResponse> testInvite1 = stub.getInvitations(-1);
        assertEquals(0, testInvite1.size());

        HashMap<Event, InviteResponse> testInvite2 = stub.getInvitations(-12);
        assertEquals(0, testInvite2.size());

        System.out.println("\n\t- Testing: getInvitations - get Invitations for event of invalid user id");
        HashMap<Event, InviteResponse> testInvite3 = stub.getInvitations(maxID + 1000);
        assertEquals(0, testInvite3.size());

        HashMap<Event, InviteResponse> testInvite4 = stub.getInvitations(maxID + 1500);
        assertEquals(0, testInvite4.size());

        System.out.println("\n\t- Testing: getInvitedUsers - get Invited Users for invalid events");
        DateTime start1 = new DateTime(2022, 8, 12, 8, 30);
        DateTime end1 = new DateTime(2022, 8, 12, 12, 30);

        SingleEvent testEvent1 = new SingleEvent("test event", -1, stub.getUser(1), "test location", start1, end1);
        SingleEvent testEvent2 = new SingleEvent("test event", maxID + 1500, stub.getUser(1), "test location", start1, end1);
        HashMap<User, InviteResponse> testInviteUser1 = stub.getInvitedUsers(testEvent1);
        HashMap<User, InviteResponse> testInviteUser2 = stub.getInvitedUsers(testEvent2);

        assertEquals(0, testInviteUser1.size());
        assertEquals(0, testInviteUser2.size());

        System.out.println("\n\t- Testing: getUser - get Users for negative id");
        User testGetUser1 = stub.getUser(-1);
        User testGetUser2 = stub.getUser(-100);

        assertNull(testGetUser1);
        assertNull(testGetUser2);

        System.out.println("\n\t- Testing: getUser - get Users for invalid id");
        User testGetUser3 = stub.getUser(maxID + 1000);
        User testGetUser4 = stub.getUser(maxID + 1500);

        assertNull(testGetUser3);
        assertNull(testGetUser4);

        System.out.println("\n\t- Testing: getAllOtherUsers - get All other Users for negative id");
        ArrayList<User> testGetAllOtherUsers = stub.getAllOtherUsers(0);

        ArrayList<User> testGetAllOtherUsers1 = stub.getAllOtherUsers(-1);
        ArrayList<User> testGetAllOtherUsers2 = stub.getAllOtherUsers(-10);

        assertEquals(testGetAllOtherUsers.size(), testGetAllOtherUsers1.size());
        assertEquals(testGetAllOtherUsers.size(), testGetAllOtherUsers2.size());

        System.out.println("\n\t- Testing: getAllOtherUsers - get All other Users for invalid id");
        testGetAllOtherUsers1 = stub.getAllOtherUsers(maxID + 1000);
        testGetAllOtherUsers2 = stub.getAllOtherUsers(maxID + 1500);

        assertEquals(testGetAllOtherUsers.size(), testGetAllOtherUsers1.size());
        assertEquals(testGetAllOtherUsers.size(), testGetAllOtherUsers2.size());

        System.out.println("\n\t- Testing: updateUser - Update Users with negative or invalid id");
        assertFalse(stub.updateUser(new User("test", -1)));
        assertFalse(stub.updateUser(new User("test", -10)));

        assertFalse(stub.updateUser(new User("test", maxID + 1000)));
        assertFalse(stub.updateUser(new User("test", maxID + 1500)));

        System.out.println("\n\t- Testing: createInvite - create Invite for users with negative or invalid id");
        assertFalse(stub.createInvite(1, -1));
        assertFalse(stub.createInvite(1, -10));

        assertFalse(stub.createInvite(1, maxID + 1000));
        assertFalse(stub.createInvite(1, maxID + 1500));

        System.out.println("\n\t- Testing: createInvite - create Invite for event with negative or invalid id");
        assertFalse(stub.createInvite(-1, 1));
        assertFalse(stub.createInvite(-10, 1));

        assertFalse(stub.createInvite(maxID + 1000, 1));
        assertFalse(stub.createInvite(maxID + 1500, 1));

        System.out.println("\n\t- Testing: getInvite - get Invite response for users with negative or invalid id");
        assertNull(stub.getInvite(1, -1));
        assertNull(stub.getInvite(1, -10));

        assertNull(stub.getInvite(1, maxID + 1000));
        assertNull(stub.getInvite(1, maxID + 1500));

        System.out.println("\n\t- Testing: getInvite - get Invite response for event with negative or invalid id");
        assertNull(stub.getInvite(-1, 1));
        assertNull(stub.getInvite(-10, 1));

        assertNull(stub.getInvite(maxID + 1000, 1));
        assertNull(stub.getInvite(maxID + 1500, 1));

        System.out.println("\n\t- Testing: deleteInvite - delete Invite for users with negative or invalid id");
        assertFalse(stub.deleteInvite(1, -1));
        assertFalse(stub.deleteInvite(1, -10));

        assertFalse(stub.deleteInvite(1, maxID + 1000));
        assertFalse(stub.deleteInvite(1, maxID + 1500));

        System.out.println("\n\t- Testing: deleteInvite - delete Invite for event with negative or invalid id");
        assertFalse(stub.deleteInvite(-1, 1));
        assertFalse(stub.deleteInvite(-10, 1));

        assertFalse(stub.deleteInvite(maxID + 1000, 1));
        assertFalse(stub.createInvite(maxID + 1500, 1));

        System.out.println("\n\t- Testing: createInactive - create inactive events for users with negative or invalid id");
        assertFalse(stub.createInactive(1, -1));
        assertFalse(stub.createInactive(1, -10));

        assertFalse(stub.createInactive(1, maxID + 1000));
        assertFalse(stub.createInactive(1, maxID + 1500));

        System.out.println("\n\t- Testing: createInactive -  create inactive events for event with negative or invalid id");
        assertFalse(stub.createInactive(-1, 1));
        assertFalse(stub.createInactive(-10, 1));

        assertFalse(stub.createInactive(maxID + 1000, 1));
        assertFalse(stub.createInactive(maxID + 1500, 1));

        System.out.println("\n\t- Testing: getInactive -  get inactive events for event with negative or invalid id");

        assertNull(stub.getInactive(-1));
        assertNull(stub.getInactive(-10));

        assertNull(stub.getInactive(maxID + 1000));
        assertNull(stub.getInactive(maxID + 1500));

        System.out.println("\n- Complete Testing: testInvalidCases");

    }


    @Test
    public void testEdgeCases() {
        User testUser;
        Event testEvent = stub.getEvent(0);
        boolean result;
        DateTime start = new DateTime(2022, 9, 9, 10, 30);
        DateTime end = start.plusHours(2);
        System.out.println("\n- Testing: Edge cases Creation of User with no name");

        try {
            stub.createUser("");
        } catch (Exception e) {
            System.out.println("Complete Testing: User with name of length 0 is not created");
        }

        System.out.println("\n- Testing: Edge cases Creation of User with name of length 1");

        testUser = stub.createUser("A");
        assertNotNull(testUser);
        assertEquals(testUser.getName(), "A");

        System.out.println("\n- Testing: Edge cases Creation of User with name having special characters");

        testUser = stub.createUser("A@@@&*&*&&8");
        assertNotNull(testUser);
        assertEquals(testUser.getName(), "A@@@&*&*&&8");

        System.out.println("\n- Testing: Edge cases Creation of Event with no name of event and location");

        try {
            stub.createEvent(new SingleEvent("", 250, testUser, "", start, end));
        } catch (Exception e) {
            System.out.println("Complete Testing: Event with invalid name and location not created.");
        }

        System.out.println("\n- Testing: creating an event with invalid interval (invalid start and end time)");

        try {
            stub.createEvent(new SingleEvent("A", 250, testUser, "A", start, start.minusHours(4)));
        } catch (Exception e) {
            System.out.println("Complete Testing: Event with invalid interval not created");
        }

        System.out.println("\n- Testing: updating event that has invalid id (not in database)");

        result = stub.updateEvent(new SingleEvent("A", 250, testUser, "A", start, end));
        assertFalse(result);

        System.out.println("\n-Complete Testing: updating event that has invalid id (not in database)");

        System.out.println("\n- Testing: replacing valid User with invalid User");

        try {
            testUser = stub.getUser(0);
            stub.updateUser(new User("", 0));

        } catch (Exception e) {
            assertEquals(testUser.getName(), "John Braico");
            System.out.println("Complete Testing: valid User is not replaced with invalid user");
        }

        System.out.println("\n- Testing: replacing valid Event with invalid Event");

        try {
            testEvent = stub.getEvent(0);
            stub.updateEvent(new SingleEvent("", 0, testUser, "", start, end));

        } catch (Exception e) {
            assertNotEquals("", testEvent.getName());
            assertNotEquals("", testEvent.getLocation());
            System.out.println("Complete Testing: valid User is not replaced with invalid user");
        }

        System.out.println("\n- Testing: deleteInvite- deleting the same Invite twice");
        assertTrue(stub.deleteInvite(1, 1));
        assertTrue(stub.deleteInvite(1, 1));
        System.out.println("\n- Complete Testing: deleteInvite- deleting the same Invite twice");

        System.out.println("\n- Testing: getInvite- changing the invite response");
        assertEquals(InviteResponse.NO_RESPONSE, stub.getInvite(1, 0));

        stub.updateInvite(1, 0, InviteResponse.ACCEPTED);

        assertEquals(InviteResponse.ACCEPTED, stub.getInvite(1, 0));
        stub.updateInvite(1, 0, InviteResponse.DECLINED);

        assertEquals(InviteResponse.DECLINED, stub.getInvite(1, 0));
        System.out.println("\n- Complete Testing: getInvite- changing the invite response");

        System.out.println("\n- Testing: creating inactive with instance of Single Event");

        testEvent = stub.getEvent(0);
        testUser = stub.getUser(0);
        result = stub.createInactive(testEvent.getId(), testUser.getId());
        assertFalse(result);

        System.out.println("\n- Complete Testing: creating inactive with instance of Single Event");

        System.out.println("\n- Testing: deleteEvent- delete the same event twice");

        assertTrue(stub.deleteEvent(1));
        assertFalse(stub.deleteEvent(1));

        System.out.println("\n- Complete Testing: deleteEvent- delete the same event twice");


    }

}

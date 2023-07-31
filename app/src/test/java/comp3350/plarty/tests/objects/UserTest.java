package comp3350.plarty.tests.objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import comp3350.plarty.objects.User;

public class UserTest {

    String validName, otherValidName;
    int validId, otherId;
    User validUser, otherValidUser;

    @Before
    public void setUp() {
        validName = "Bohn Jraico";
        otherValidName = "Gob Ruderian";
        validId = 1;
        otherId = 2;
        validUser = new User(validName, validId);
        otherValidUser = new User(otherValidName, otherId);
    }

    @Test
    public void testTypicalCasesGetters() {
        System.out.println("\n- Testing: UserTest - Getters");

        System.out.println("\n- Testing: getName");
        assertEquals(validName, validUser.getName());

        System.out.println("\n- Testing: getId");
        assertEquals(validId, validUser.getId());

        System.out.println("\n- Completed Testing: UserTest - Getters");
    }

    @Test
    public void testInvalidUserCreation() {
        System.out.println("\n- Testing: UserTest - Invalid Creation");

        // Ensure that an exception is thrown when invalid names are passed to the constructor
        System.out.println("\n- Testing: Constructor - Empty name");
        try {
            User user = new User("", 3);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Spaces Name");
        try {
            User user = new User("     ", 5);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - New lines");
        try {
            User user = new User("\n\n\n", 5);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Null Name");
        try {
            User user = new User(null, 4);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: Constructor - Negative id");
        try {
            User user = new User(null, -34);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Completed Testing: UserTest - Invalid Creation");
    }

    @Test
    public void testTypicalCasesSetters() {
        System.out.println("\n- Testing: UserTest - Setters");

        System.out.println("\n- Testing: setName");
        validUser.setName(otherValidName);
        assertEquals(otherValidName, validUser.getName());

        System.out.println("\n- Completed Testing: UserTest - Setters");
    }

    @Test
    public void testInvalidSetters() {
        System.out.println("\n- Testing: UserTest - Invalid Setters");

        // Try setting invalid name values, ensure that an exception is thrown
        System.out.println("\n- Testing: setName - Empty name");
        try {
            validUser.setName("");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setName - Spaces Name");
        try {
            validUser.setName("         ");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setName - New lines");
        try {
            validUser.setName("\n\n\n\n\n\n\n");
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Testing: setName - Null Name");
        try {
            validUser.setName(null);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }

        System.out.println("\n- Completed Testing: UserTest - Invalid Setters");
    }

    @Test
    public void testTypicalCasesEquals() {
        System.out.println("\n\t- Testing: UserTest - Equals method\n");

        System.out.println("\n- Testing: equals - valid equals");
        assertEquals(validUser, validUser);

        System.out.println("\n- Testing: equals - user not equals");
        assertNotEquals(validUser, otherValidUser);
        assertNotEquals(new User("hello", 3), new User("hello Im not the same", 4));

        System.out.println("\n\t- Completed Testing: UserTest - Equals method\n");
    }

    @Test
    public void testEdgeCasesEquals() {
        System.out.println("\n\t- Testing: UserTest - Equals Edge Cases\n");

        System.out.println("\n- Testing: equals - same name diff id");
        assertNotEquals(new User("hello", 3), new User("hello", 4));

        System.out.println("\n- Testing: equals - diff name same id");
        assertEquals(new User("hello dude", 3), new User("hello", 3));

        System.out.println("\n\t- Completed Testing:U serTest - Equals Edge Cases\n");
    }

    @Test
    public void testToString() {
        System.out.println("\n- Testing: EventTest - ToString");

        System.out.println("\n- Testing: toString - Valid Users");
        assertEquals("\nName: Bohn Jraico\n\tID: 1", validUser.toString());

        assertEquals("\nName: Gob Ruderian\n\tID: 2", otherValidUser.toString());

        System.out.println("\n- Completed Testing: EventTest - ToString");
    }

}

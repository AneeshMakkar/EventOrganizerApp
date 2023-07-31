package comp3350.plarty.tests.objects;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import comp3350.plarty.objects.InviteResponse;

public class InviteResponseTest  {

    @Test
    public void testTypicalCases() {
        System.out.println("\n- Testing: InviteResponse");

        // Test basic enum values
        assertEquals(4,InviteResponse.values().length);

        assertEquals("NO_RESPONSE", InviteResponse.NO_RESPONSE.toString());
        assertEquals("ACCEPTED", InviteResponse.ACCEPTED.toString());
        assertEquals("DECLINED", InviteResponse.DECLINED.toString());
        assertEquals("MAYBE", InviteResponse.MAYBE.toString());

        System.out.println("\n- Completed Testing: InviteResponse");
    }
}
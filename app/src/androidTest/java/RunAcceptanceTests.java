import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import comp3350.plarty.tests.acceptance.FriendInvitationTest;
import comp3350.plarty.tests.acceptance.ScheduleUpdateTests;
import comp3350.plarty.tests.acceptance.TimeSearchingTests;
import comp3350.plarty.tests.acceptance.ViewScheduleTests;


@RunWith(Suite.class)
@Suite.SuiteClasses({
        FriendInvitationTest.class,
        ScheduleUpdateTests.class,
        TimeSearchingTests.class,
        ViewScheduleTests.class
})

public class RunAcceptanceTests {
}
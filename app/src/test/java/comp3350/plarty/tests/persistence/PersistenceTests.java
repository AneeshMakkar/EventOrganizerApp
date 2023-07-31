package comp3350.plarty.tests.persistence;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        DataAccessTest.class,
        DataStubTests.class
})

public class PersistenceTests {

}

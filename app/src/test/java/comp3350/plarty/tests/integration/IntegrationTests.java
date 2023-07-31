package comp3350.plarty.tests.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        BusinessPersistenceSeamTest.class,
        DataAccessHSQLDBTest.class
})

public class IntegrationTests {

}

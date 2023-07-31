package comp3350.plarty.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import comp3350.plarty.tests.integration.IntegrationTests;

@RunWith(Suite.class)
@SuiteClasses({
        IntegrationTests.class
})
public class RunIntegrationTests {

}

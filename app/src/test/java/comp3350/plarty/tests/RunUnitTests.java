package comp3350.plarty.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import comp3350.plarty.tests.business.BusinessTests;
import comp3350.plarty.tests.objects.ObjectTests;
import comp3350.plarty.tests.persistence.PersistenceTests;

@RunWith(Suite.class)
@SuiteClasses({
        BusinessTests.class,
        ObjectTests.class,
        PersistenceTests.class
})

public class RunUnitTests {

}

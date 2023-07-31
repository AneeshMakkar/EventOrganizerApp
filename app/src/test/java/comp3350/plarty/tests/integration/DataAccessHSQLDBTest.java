package comp3350.plarty.tests.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import comp3350.plarty.application.Services;
import comp3350.plarty.persistence.DataAccess;
import comp3350.plarty.tests.persistence.DataAccessTest;

public class DataAccessHSQLDBTest {
    DataAccess dataAccess;

    @Before
    public void setUp() {
        Services.closeDataAccess();
        System.out.println("\nStarting Integration test DataAccess (using default DB)");
    }

    @After
    public void tearDown() {
        Services.closeDataAccess();
        System.out.println("Finished Integration test DataAccess (using default DB)");
    }

    @Test
    public void testDataAccess() {
        Services.createDataAccess();
        dataAccess = Services.getDataAccess();
        DataAccessTest.dataAccessTest(dataAccess);
    }
}

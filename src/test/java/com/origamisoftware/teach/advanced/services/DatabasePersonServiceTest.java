package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.Person;
import com.origamisoftware.teach.advanced.model.PersonTest;
import com.origamisoftware.teach.advanced.util.DatabaseUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for the DatabasePersonService
 */
public class DatabasePersonServiceTest {

    private PersonService personService;

    private void initDb() throws Exception {
        DatabaseUtils.initializeDatabase(DatabaseUtils.initializationFile);
    }

    // do not assume db is correct
    @Before
    public void setUp() throws Exception {
        // we could also copy db state here for later restore before initializing
        initDb();
        personService = ServiceFactory.getPersonServiceInstance();
    }

    // clean up after ourselves. (this could also restore db from initial state
    @After
    public void tearDown() throws Exception {
        initDb();
    }

    @Test
    public void testGetInstance() {
        assertNotNull("Make sure PersonService is available", personService);
    }

    @Test
    public void testGetPerson() throws PersonServiceException {
        List<Person> personList = personService.getPerson();
        assertFalse("Make sure we get some Person objects from service", personList.isEmpty());
    }

    @Test
    public void testAddOrUpdatePerson()throws PersonServiceException {
        Person newPerson = PersonTest.createPerson();
        personService.addOrUpdatePerson(newPerson);
        List<Person> personList = personService.getPerson();
        boolean found = false;
        for (Person person : personList) {
            Timestamp returnedBirthDate = person.getBirthDate();
            Calendar returnCalendar = Calendar.getInstance();
            returnCalendar.setTimeInMillis(returnedBirthDate.getTime());
            if (returnCalendar.get(Calendar.MONTH) == PersonTest.birthDayCalendar.get(Calendar.MONTH)
                    &&
                    returnCalendar.get(Calendar.YEAR) == PersonTest.birthDayCalendar.get(Calendar.YEAR)
                    &&
                    returnCalendar.get(Calendar.DAY_OF_MONTH) == PersonTest.birthDayCalendar.get(Calendar.DAY_OF_MONTH)
                    &&
                    person.getLastName().equals(PersonTest.lastName)
                    &&
                    person.getFirstName().equals(PersonTest.firstName)) {
                found = true;
                break;
            }
        }
        assertTrue("Found the person we added", found);
    }

    @Test
    public void testGetStocksByPerson() throws PersonServiceException {
        Person person = PersonTest.createPerson();
        List<String> symbols = personService.getStocks(person);
        // make the person have all the hobbies
        for (String symbol : symbols) {
            personService.addStocksToPerson(symbol, person);
        }
        List<String> symbolList = personService.getStocks(person);
        for (String symbol : symbols) {
            boolean removed = symbolList.remove(symbol);
            assertTrue("Verify that the hobby was found on the list", removed);
        }
        // if  hobbyList is empty then we know the lists matched.
        assertTrue("Verify the list of returned hobbies match what was expected ", symbolList.isEmpty());

    }


}


package com.origamisoftware.teach.advanced.model;

import org.junit.Test;

import java.sql.Timestamp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Unit test for PersonStock class
 */
public class PersonStockTest {

    /**
     * Testing helper method for generating PersonStock test data
     *
     * @return a PersonStock object that uses Person and Symbol
     * return from their respective create method.
     */
    public static PersonStock createPersonStock() {
        Person person = PersonTest.createPerson();
        String symbol = "GOOG";
        return new PersonStock(person, symbol);
    }

    @Test
    public void testPersonHobbiesGetterAndSetters() {
        Person person = PersonTest.createPerson();
        PersonStock personStock = new PersonStock();
        int id = 10;
        personStock.setId(id);
        personStock.setPerson(person);
        personStock.setSymbol("GOOG");
        assertEquals("person matches", person, personStock.getPerson());
        assertEquals("stock matches", "GOOG", personStock.getSymbol());
        assertEquals("id matches", id, personStock.getId());
    }

    @Test
    public void testEqualsNegativeDifferentPerson() {
        PersonStock personStock = createPersonStock();
        personStock.setId(10);
        personStock.setSymbol("GOOG");
        Person person = new Person();
        Timestamp birthDate = new Timestamp(PersonTest.birthDayCalendar.getTimeInMillis() + 10000);
        person.setBirthDate(birthDate);
        person.setFirstName(PersonTest.firstName);
        person.setLastName(PersonTest.lastName);
        PersonStock personStock2 = new PersonStock(person, "GOOG");
        assertFalse("Different person", personStock.equals(personStock2));
    }

    @Test
    public void testEquals() {
        PersonStock personStock = createPersonStock();
        assertTrue("Same objects are equal", personStock.equals(createPersonStock()));
    }

    @Test
    public void testToString() {
        PersonStock personStock = createPersonStock();
        assertTrue("toString has lastName", personStock.toString().contains(PersonTest.lastName));
        assertTrue("toString has person", personStock.toString().contains(PersonTest.firstName));
        assertTrue("toString has symbol", personStock.toString().contains("GOOG"));

    }

}

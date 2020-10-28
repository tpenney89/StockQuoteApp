package com.origamisoftware.teach.advanced.model;

import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for the Person class
 */
public class PersonTest {

    public  static final Calendar birthDayCalendar = Calendar.getInstance();

    static {
        birthDayCalendar.set(1970, Calendar.JANUARY, 01);
    }

    public static final String firstName = "Laura";
    public  static final String lastName = "Berkholtz";
    public static final Timestamp birthDate = new Timestamp(birthDayCalendar.getTimeInMillis());

    /**
     * Testing helper method for generating Person test data
     *
     * @return a Person object that uses static constants for data.
     */
    public static Person createPerson() {
        Person person = new Person();
        person.setBirthDate(birthDate);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        return person;
    }

    @Test
    public void testPersonGettersAndSetters() {
        Person person = createPerson();
        int id = 10;
        person.setId(id);
        assertEquals("first name matches", firstName, person.getFirstName());
        assertEquals("last name matches", lastName, person.getLastName());
        assertEquals("birthday matches", birthDate, person.getBirthDate());
        assertEquals("id matches", id, person.getId());

    }

}
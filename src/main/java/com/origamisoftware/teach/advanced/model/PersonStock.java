package com.origamisoftware.teach.advanced.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Models a table the combines person with their stocks.
 */
@Entity
@Table(name = "person_stocks", catalog = "stocks")
public class PersonStock {
    private int id;
    private Person person;
    private String person_symbol;

    /**
     * Create a PersonStock that needs to be initialized
     */
    public PersonStock() {
        // this empty constructor is required by hibernate framework

    }

    /**
     * Create a valid PersonStock
     *
     * @param person the person to assign the hobby to
     * @param symbol  the stock to associate the person with
     */
    public PersonStock(Person person, String symbol) {
        setSymbol(symbol);
        setPerson(person);
    }

    /**
     * Primary Key - Unique ID for a particular row in the person_hobby table.
     *
     * @return an integer value
     */
    @Id
    @Column(name = "ID", nullable = false, insertable = true, updatable = true)
    public int getId() {
        return id;
    }

    /**
     * Set the unique ID for a particular row in the person_hobby table.
     * This method should not be called by client code. The value is managed internally.
     *
     * @param id a unique value.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return get the Person associated with this hobby
     */
    @ManyToOne
    @JoinColumn(name = "person_id", referencedColumnName = "ID", nullable = false)
    public Person getPerson() {
        return person;
    }

    /**
     * Specify the Person associated with the hobby.
     *
     * @param person a person instance
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     *
     * @return get the Symbol associated with this Person
     */

    public String getSymbol() {
        return person_symbol;
    }

    /**
     * Specify the Symbol associated with the Person.
     *
     * @param symbol a stock symbol
     */
    public void setSymbol(String symbol) {
        this.person_symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonStock that = (PersonStock) o;

        if (id != that.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + person.hashCode();
        result = 31 * result + person_symbol.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PersonStock{" +
                "id=" + id +
                ", person=" + person +
                ", symbol=" + person_symbol +
                '}';
    }
}

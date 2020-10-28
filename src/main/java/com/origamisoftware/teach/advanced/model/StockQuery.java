package com.origamisoftware.teach.advanced.model;

import org.apache.http.annotation.Immutable;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.util.Calendar;

/**
 * This class is used to get a single query to stock service.
 */
@Immutable
public class StockQuery extends StockData{

    private String symbol;
    private Calendar from;
    private Calendar until;
    private String interval;

    /**
     * Create a new instance from string data. This constructor will convert
     * dates described as a String to Date objects.
     *
     * @param symbol the stock symbol
     * @param from   the start date as a string in the form of MM/DD/YYYY
     * @param until  the end date as a string in the form of MM/DD/YYYY
     * @throws ParseException if the format of the date String is incorrect. If this happens
     *                        the only recourse is to try again with a correctly formatted String.
     */
    public StockQuery(@NotNull String symbol, @NotNull String from, @NotNull String until, String interval) throws ParseException {
        super();
        this.symbol = symbol;
        this.from = Calendar.getInstance();
        this.until = Calendar.getInstance();
        System.out.println(simpleDateFormat);
        this.from.setTime(simpleDateFormat.parse(from));
        this.until.setTime(simpleDateFormat.parse(until));
        this.interval = interval;
    }

    /**
     * returns the <CODE>symbol</CODE> in the current instance of StockQuery
     * @return get the stock symbol associated with this query
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * returns the <CODE>from</CODE> date in the current instance of StockQuery
     * @return get the start Calendar associated with this query
     */
    public Calendar getFrom() {
        return from;
    }

    /**
     * returns the <CODE>until</CODE> date in the current instance of StockQuery
     * @return get the end Calendar associated with this query
     */
    public Calendar getUntil() {
        return until;
    }

    /**
     * returns the <CODE>interval</CODE> date in the current instance of StockQuery
     * @return get the end Calendar associated with this query
     */
    public String getInterval() {
        return interval;
    }
}

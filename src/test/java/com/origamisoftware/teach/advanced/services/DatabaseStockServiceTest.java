package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.StockData;
import com.origamisoftware.teach.advanced.model.StockQuote;
import com.origamisoftware.teach.advanced.util.DatabaseInitializationException;
import com.origamisoftware.teach.advanced.util.DatabaseUtils;
import com.origamisoftware.teach.advanced.util.Interval;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;


/**
 * Unit tests for the DatabaseStockService
 */
public class DatabaseStockServiceTest extends StockData {

    private DatabaseStockService databaseStockService;

    @Before
    public void setUp() throws DatabaseInitializationException {
        DatabaseUtils.initializeDatabase(DatabaseUtils.initializationFile);
        databaseStockService = new DatabaseStockService();

    }

    @Test
    public void testGetQuote() throws Exception {
        DatabaseStockService databaseStockService = new DatabaseStockService();
        String symbol = "APPL";
        StockQuote stockQuote = databaseStockService.getQuote(symbol);
        assertNotNull("Verify we can get a stock quote from the db", stockQuote);
        assertEquals("Make sure the symbols match", symbol, stockQuote.getSymbol());
    }

    @Test
    public void testGetQuoteWithIntervalBasic() throws Exception {
        String symbol = "GOOG";
        String fromStringDate = "2000-02-10 00:00:01";
        String untilStringDate = "2015-02-03 00:00:01";

        Calendar fromCalendar = makeCalendarFromString(fromStringDate);
        Calendar untilCalendar = makeCalendarFromString(untilStringDate);

        List<StockQuote> stockQuotes = databaseStockService.getQuote(symbol, fromCalendar, untilCalendar, Interval.DAY);
        assertFalse("verify stock quotes where returned", stockQuotes.isEmpty());
    }


    @Test
    public void testGetQuoteWithinRange() throws Exception {

        String fromDateString = "2015-02-10 00:01:01";
        String endDateString = "2015-02-10 00:08:01";
        String symbol = "AMZN";

        String sql = "INSERT INTO quotes (symbol,time,price) VALUES ('AMZN','" + fromDateString + "','363.21');";
        DatabaseUtils.executeSQL(sql);

        sql = "INSERT INTO quotes (symbol,time,price) VALUES ('AMZN','2015-02-10 00:04:01','250.21');";
        DatabaseUtils.executeSQL(sql);

        sql = "INSERT INTO quotes (symbol,time,price) VALUES ('AMZN','2015-02-10 00:06:01','251.21');";
        DatabaseUtils.executeSQL(sql);

        sql = "INSERT INTO quotes (symbol,time,price) VALUES ('AMZN','" + endDateString + "','253.21');";
        DatabaseUtils.executeSQL(sql);

        Calendar fromCalendar = makeCalendarFromString(fromDateString);
        Calendar untilCalendar = makeCalendarFromString(endDateString);

        List<StockQuote> stockQuotes = databaseStockService.getQuote(symbol, fromCalendar, untilCalendar, Interval.DAY);
        assertEquals("got back expected number of stockquotes for one day interval", 1, stockQuotes.size());

        stockQuotes = databaseStockService.getQuote(symbol, fromCalendar, untilCalendar, Interval.HOUR);
        assertEquals("got back expected number of stockquotes for one minute interval", 2, stockQuotes.size());
    }

    /**
     * Handy dandy helper method that converts Strings in the format of   StockData.dateFormat
     * to Calendar instances set to the date expressed in the string.
     *
     * @param dateString a data and time in this format: StockData.dateFormat
     * @return a calendar instance set to the time in the string.
     * @throws ParseException if the string is not in the correct format, we can't tell what
     *                        time it is, and therefore can't make a calender set to that time.
     */
    private Calendar makeCalendarFromString(String dateString) throws ParseException {
        DateFormat format = new SimpleDateFormat(StockData.dateFormat, Locale.ENGLISH);
        Date date = format.parse(dateString);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;

    }

}

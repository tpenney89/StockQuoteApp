package com.origamisoftware.teach.advanced.model;

import org.junit.Test;
import java.text.ParseException;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;

/**
 * Unit test for StockQuery Class
 */
public class StockQueryTest {

    @Test
    public void testBasicConstruction() throws Exception {
        String symbol = "APPL";
        StockQuery stockQuery = new StockQuery(symbol, "2011-10-29 12:12:1","2014-10-29 12:12:1", "DAILY");
        assertEquals("Verify construction", symbol, stockQuery.getSymbol());

    }
}

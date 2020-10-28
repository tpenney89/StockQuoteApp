package com.origamisoftware.teach.advanced.services;

import jdk.nashorn.internal.ir.annotations.Immutable;
import com.origamisoftware.teach.advanced.model.StockQuote;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A factory that returns a <CODE>StockService</CODE> instance.
 */
@Immutable
public class ServiceFactory {

    /**
     * Prevent instantiations
     */
    private ServiceFactory() {}

    /**
     * returns a DatabaseStockService instance
     * @return get a <CODE>StockService</CODE> instance
     */

    public static PersonService getPersonServiceInstance() {
        return new DatabasePersonService();
    }
    /**
     * returns a DatabaseStockService instance
     * @return get a <CODE>StockService</CODE> instance
     */


    public static StockService getStockServiceInstance() {
        return new DatabaseStockService();
    }

}

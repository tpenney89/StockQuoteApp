package com.origamisoftware.teach.advanced.services;

import com.origamisoftware.teach.advanced.model.StockQuote;
import com.origamisoftware.teach.advanced.model.StockData;
import com.origamisoftware.teach.advanced.util.DatabaseConnectionException;
import com.origamisoftware.teach.advanced.util.DatabaseUtils;
import com.origamisoftware.teach.advanced.util.Interval;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * An implementation of the StockService interface that gets
 * stock data from a database.
 */
public class DatabaseStockService implements StockService {


    /**
     * Return the current price for a share of stock  for the given symbol
     *
     * @param symbol the stock symbol of the company you want a quote for.
     *               e.g. APPL for APPLE
     * @return a  <CODE>BigDecimal</CODE> instance
     * @throws StockServiceException if using the service generates an exception.
     * If this happens, trying the service may work, depending on the actual cause of the
     * error.
     */
    @Override
    public StockQuote getQuote(String symbol) throws StockServiceException {
        // todo - this is a pretty lame implementation why?
        // todo - answer because it only returns the first quote it finds
        List<StockQuote> stockQuotes = null;
        try {
            Connection connection = DatabaseUtils.getConnection();
            Statement statement = connection.createStatement();
            String queryString = "select * from quotes where symbol = '" + symbol + "'";

            ResultSet resultSet = statement.executeQuery(queryString);
            stockQuotes = new ArrayList<>(resultSet.getFetchSize());
            while (resultSet.next()) {
                String symbolValue = resultSet.getString("symbol");
                Date time = resultSet.getDate("time");
                BigDecimal price = resultSet.getBigDecimal("price");
                stockQuotes.add(new StockQuote(price, time, symbolValue));
            }

        } catch (DatabaseConnectionException | SQLException exception) {
            throw new StockServiceException(exception.getMessage(), exception);
        }
        if (stockQuotes.isEmpty()) {
            throw new StockServiceException("There is no stock data for:" + symbol);
        }
        return stockQuotes.get(0);
    }

    /**
     * Get a historical list of stock quotes for the provide symbol
     * todo - split out duplicate code into another method when it is all working and if have time
     * todo - rewrite this to get just one stock quote for every date. Right now it works beccause there is not more than one quote per day in the database
     * @param symbol the stock symbol to search for
     * @param from   the date of the first stock quote
     * @param until  the date of the last stock quote
     * @return a list of StockQuote instances
     * @throws StockServiceException if using the service generates an exception.
     *                               If this happens, trying the service may work, depending on the actual cause of the
     *                               error.
     */
    @Override
    public List<StockQuote> getQuote(String symbol, Calendar from, Calendar until) throws StockServiceException {
        Calendar workingdate;
        workingdate = from;

        List<StockQuote> stockQuotes = null;


        try {
            //connect to local database
            Connection connection = DatabaseUtils.getConnection();
            Statement statement = connection.createStatement();
            String queryString = "select * from quotes where symbol = '" + symbol + "'";

            ResultSet resultSet = statement.executeQuery(queryString);
            stockQuotes = new ArrayList<>(resultSet.getFetchSize());
            //get stock quotes, increase workingdate by one until it reaches the until date
            while ((workingdate.before(until) || workingdate.equals(until)) && resultSet.next()) {

                String symbolValue = resultSet.getString("symbol");
                Date time = resultSet.getDate("time");
                BigDecimal price = resultSet.getBigDecimal("price");
                stockQuotes.add(new StockQuote(price, time, symbolValue));
                workingdate.add(Calendar.DAY_OF_YEAR, 1);

            }
        }

        catch (DatabaseConnectionException | SQLException exception) {
            throw new StockServiceException(exception.getMessage(), exception);
        }
        if (stockQuotes.isEmpty()) {
            throw new StockServiceException("There is no stock data for:" + symbol);
        }

        return stockQuotes;
    }
    /**
     * Get a historical list of stock quotes for the provide symbol
     * todo - split out duplicate code when it is all working
     * todo  this only works because of the way the database is set up. Need to rewrite, sort the result in the query statement, Then check the time on the result sets. This should be broken out into methods for each interval
     * @param symbol the stock symbol to search for
     * @param from   the date of the first stock quote
     * @param until  the date of the last stock quote
     * @param interval­ the number of StockQuotes to get. E.g. if Interval.DAILY was
     *        specified one StockQuote per day will be returned.
     * @return a list of StockQuote instances
     * @throws StockServiceException if using the service generates an exception.
     *                               If this happens, trying the service may work, depending on the actual cause of the
     *                               error.
     */
    @Override
    public List<StockQuote> getQuote(String symbol, Calendar from, Calendar until, Interval interval) throws StockServiceException {
        Calendar workingdate;
        workingdate = from;
        workingdate.set(Calendar.HOUR_OF_DAY, 0);
        workingdate.set(Calendar.MINUTE, 0);
        workingdate.set(Calendar.SECOND, 0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateFrom =sdf.format(from.getTime());
        String dateUntil =sdf.format(until.getTime());


        List<StockQuote> stockQuotes = null;


        try {
            Connection connection = DatabaseUtils.getConnection();
            Statement statement = connection.createStatement();
            String queryString = "select * from quotes where symbol = '" + symbol + "' and time between '" + dateFrom + "' and '" + dateUntil + "'" ;
            ResultSet resultSet = statement.executeQuery(queryString);
            stockQuotes = new ArrayList<>(resultSet.getFetchSize());
            StockQuote previousStockQuote = null;
            Calendar calendar = Calendar.getInstance();
            while (resultSet.next()) {
                String symbolValue = resultSet.getString("symbol");
                Timestamp timeStamp = resultSet.getTimestamp("time");
                calendar.setTimeInMillis(timeStamp.getTime());
                BigDecimal price = resultSet.getBigDecimal("price");
                java.util.Date time = calendar.getTime();
                StockQuote currentStockQuote = new StockQuote(price, time, symbolValue);

                if (previousStockQuote == null) { // first time through always add stockquote

                    stockQuotes.add(currentStockQuote);

                } else if (isInterval(currentStockQuote.getDate(),
                        interval,
                        previousStockQuote.getDate())) {

                    stockQuotes.add(currentStockQuote);
                }

                previousStockQuote = currentStockQuote;
            }

        } catch (DatabaseConnectionException | SQLException exception) {
            throw new StockServiceException(exception.getMessage(), exception);
        }
        if (stockQuotes.isEmpty()) {
            throw new StockServiceException("There is no stock data for:" + symbol);
        }

        return stockQuotes;
    }


    /**
     * Returns true if the currentStockQuote has a date that is later by the time
     * specified in the interval value from the previousStockQuote time.
     *
     * @param endDate   the end time
     * @param interval  the period of time that must exist between previousStockQuote and currentStockQuote
     *                  in order for this method to return true.
     * @param startDate the starting date
     * @return
     */
    private boolean isInterval(java.util.Date endDate, Interval interval, java.util.Date startDate) {
        Calendar startDatePlusInterval = Calendar.getInstance();
        startDatePlusInterval.setTime(startDate);
        startDatePlusInterval.add(Calendar.MINUTE, interval.getMinutes());
        return endDate.after(startDatePlusInterval.getTime());
    }

}

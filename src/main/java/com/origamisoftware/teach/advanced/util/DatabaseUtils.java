package com.origamisoftware.teach.advanced.util;

import com.ibatis.common.jdbc.ScriptRunner;
import com.origamisoftware.teach.advanced.services.DatabasePersonService;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A class that contains database related utility methods.
 */
public class DatabaseUtils {

    public static final String initializationFile = "./src/main/sql/stocks_db_initialization.sql" ;

    // in a real program these values would be a configurable property and not hard coded.
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/stocks";

    //  Database credentials, again in a real program these values would be a configurable property and not hard coded.
    private static final String USER = "monty";
    private static final String PASS = "some_pass";

    /**
     *
     * @return a connection to the DBMS which is used for DBMS
     *
     * @throws DatabaseConnectionException if a connection cannot be made.
     */
    public static Connection getConnection() throws DatabaseConnectionException {
        Connection connection = null;
        try {
            Class.forName(JDBC_DRIVER);
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            // an example of throwing an exception appropriate to the abstraction.
        } catch (ClassNotFoundException | SQLException e) {
            String message = e.getMessage();
            throw new DatabaseConnectionException("Could not connection to database." + message, e);
        }
        return connection;
    }

    /**
     * A utility method that runs a db initialize script.
     *
     * @param initializationScript full path to the script to run to create the schema
     * @throws DatabaseInitializationException
     */
    public static void initializeDatabase(String initializationScript) throws DatabaseInitializationException {

        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);
            ScriptRunner runner = new ScriptRunner(connection, false, false);
            InputStream inputStream = new FileInputStream(initializationScript);

            InputStreamReader reader = new InputStreamReader(inputStream);

            runner.runScript(reader);
            reader.close();
            connection.commit();
            connection.close();

        } catch (DatabaseConnectionException | SQLException | IOException e) {
            throw new DatabaseInitializationException("Could not initialize db because of:"
                    + e.getMessage(), e);
        }
    }

    /**
     * Execute SQL code
     * @param someSQL  the code to execute
     * @return true if the operation succeeded.
     * @throws DatabaseException if accessing and executing the sql failed in an unexpected way.
     *
     */
    public static boolean executeSQL(String someSQL) throws DatabaseException {
        Connection connection = null;
        boolean returnValue = false;
        try {
            connection = DatabaseUtils.getConnection();
            Statement statement = connection.createStatement();
            returnValue = statement.execute(someSQL);
        } catch (DatabaseConnectionException | SQLException e) {
            throw new DatabaseException(e.getMessage(), e);
        }
        return returnValue;
    }

    private static SessionFactory sessionFactory;
    private static Configuration configuration;

    /*
     * @return SessionFactory for use with database transactions
     */
    public static SessionFactory getSessionFactory() {

        // singleton pattern
        synchronized (DatabasePersonService.class) {
            if (sessionFactory == null) {

                Configuration configuration = getConfiguration();

                ServiceRegistry serviceRegistry = new ServiceRegistryBuilder()
                        .applySettings(configuration.getProperties())
                        .buildServiceRegistry();

                sessionFactory = configuration.buildSessionFactory(serviceRegistry);

            }
        }
        return sessionFactory;
    }
    /**
     * Create a new or return an existing database configuration object.
     *
     * @return a Hibernate Configuration instance.
     */
    private static Configuration getConfiguration() {

        synchronized (DatabaseUtils.class) {
            if (configuration == null) {
                configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");
            }
        }
        return configuration;
    }

}

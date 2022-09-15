package helper;

import java.sql.Connection;
import java.sql.DriverManager;
/**this class stores the database connection info and provides a connection*/
public abstract class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // putting together the URL using a localhost
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    private static Connection connection;  // Connection Interface


    /**opens a connection to the SQL database*/
    public static void openConnection()
    {
        try {
            Class.forName(driver); // Locate Driver
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // Reference Connection object
            System.out.println("Connection successful!");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /**returns the connection object for use in PreparedStatements*/
    public static Connection getConnection(){
        return connection;
    }

    /**Closes the connection to the SQL database*/
    public static void closeConnection() {  //closes the connection. will only run when the program closes (at the end of main)
        try {
            connection.close();
            System.out.println("Connection closed!");
        }
        catch(Exception e){}
    }
}

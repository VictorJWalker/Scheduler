package POJOs;

import java.time.Month;
/**This class will hold the results of a query to count the amount of appointments for each month. */
public class MonthResults extends QueryResults{
    Month month;
    /**Creates an object with a given month and starts its count at 1. */
    public MonthResults(Month m){
        month = m;
        amount = 1;
    }
    /**Returns the month of this object. */
    public Month getMonth() {
        return month;
    }
}

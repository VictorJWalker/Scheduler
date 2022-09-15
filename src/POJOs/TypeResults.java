package POJOs;
/**this class will hold the results of a query to count the amount of appointments of each type*/
public class TypeResults extends QueryResults{
    String type;
    /**Creates an object with a given type and starts its count at 1. */
    public TypeResults(String t){
        type = t;
        amount = 1;
    }
    /**Returns this object's type. */
    public String getType() {
        return type;
    }
}

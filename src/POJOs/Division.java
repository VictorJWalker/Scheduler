package POJOs;

/**This class handles data for first level divisions. */
public class Division {         //
    private int divisionID;
    private String divisionName;
    private int countryID;


    /**Standard constructor. */
    public Division(int id, String name, int cid){
        setDivisionID(id);
        setDivisionName(name);
        setCountryID(cid);
    }
    /**Determines how this object will be displayed as a String. */
    public String toString(){
        return divisionName;
    }

    //getters and setters
    /**Returns the country ID. */
    public int getCountryID(){
        return countryID;
    }
    /**Allows one to change the country ID. */
    public void setCountryID(int cid){
        countryID = cid;
    }
    /**Returns the division ID. */
    public int getDivisionID() {
        return divisionID;
    }
    /**Allows one to change the division ID. */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }
    /**Allows one to change the division name. */
    public void setDivisionName(String divisionName) {
        this.divisionName = divisionName;
    }



}

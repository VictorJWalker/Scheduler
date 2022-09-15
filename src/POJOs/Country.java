package POJOs;

import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
/**this class is responsible for handling data for Countries*/
public class Country {
    private int countryID;
    private String countryName;

    /**Standard constructor. */
    public Country(int id, String name){
        setCountryID(id);
        setCountryName(name);
    }
    /**Constructor for use without values*/
    public Country(){
        countryID = -1;
        countryName = "defaultCountry";
    }
    /**Returns this object's Country ID. */
    public int getCountryID() {
        return countryID;
    }
    /**Allows one to change this object's country ID. */
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
    /**Allows one to change this object's country name. */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**Determines how this object will be displayed as a string. */
    public String toString(){       //returns country name if called as a string
        return this.countryName;
    }


}

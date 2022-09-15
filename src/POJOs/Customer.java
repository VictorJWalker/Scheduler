package POJOs;

import helper.JDBC;
import javafx.collections.ObservableList;

import java.sql.*;
/**This class handles the data for Customers. */
public class Customer {
    private int customerID;
    private String name;
    private String address;
    private String zip;
    private String phone;
    private int divID;
    private int countryID;
    /**Standard constructor. */
    public Customer(int id, String name, String address, String zip, String phone, int divID, int countryID){
        setCustomerID(id);
        setName(name);
        setAddress(address);
        setZip(zip);
        setPhone(phone);
        setDivID(divID);
        setCountryID(countryID);
    }
    /**Updates this customer's information in the database. */
    public void updateToDatabase() throws SQLException {

        String sql = "UPDATE customers SET Customer_Name = ?,Address = ?,Postal_Code = ?, Phone = ?,Division_ID = ? WHERE Customer_ID = ?";

        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ps.setString(1,name);
        ps.setString(2,address);
        ps.setString(3,zip);
        ps.setString(4,phone);
        ps.setInt(5,divID);
        ps.setInt(6, customerID);
        ps.executeUpdate();
    }

    //getters and setters
    /**Returns the country ID. */
    public int getCountryID() {
        return countryID;
    }
    /**Allows one to change the country ID. */
    public void setCountryID(int countryID) {
        this.countryID = countryID;
    }
    /**Returns the customer ID.*/
    public int getCustomerID() {
        return customerID;
    }
    /**Allows one to change the customer ID. */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    /**Returns the customer's name. */
    public String getName() {
        return name;
    }
    /**Allows one to change the customer's name. */
    public void setName(String name) {
        this.name = name;
    }
    /**Returns the customer's address. */
    public String getAddress() {
        return address;
    }
    /**Allows one to change the customer's address. */
    public void setAddress(String address) {
        this.address = address;
    }
    /**Returns the customer's postal code.*/
    public String getZip() {
        return zip;
    }
    /**Allows one to change the customer's postal code. */
    public void setZip(String zip) {
        this.zip = zip;
    }
    /**Returns the customer's phone number. */
    public String getPhone() {
        return phone;
    }
    /**Allows one to change the customer's phone number. */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /**Allows one to change the customer's division ID. */
    public void setDivID(int id){
        divID = id;
    }
    /**Returns the ID of the region the customer lives in. */
    public int getDivID(){
        return divID;
    }
}

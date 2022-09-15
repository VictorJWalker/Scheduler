package POJOs;

import helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
/**This class stores data for appointments*/
public class Appointment {

    private int ID;
    private String title;
    private String description;
    private String location;
    private String type;
    private LocalDateTime start;
    private LocalDateTime end;
    private int customerID;
    private int userID;
    private int contactID;


    /**Constructor with Timestamps. This constructor is primarily used when inputting directly from the SQL database.*/
    public Appointment(int id, String title, String desc, String loc, String type, Timestamp startTime, Timestamp endTime, int cuid, int uid, int coid){
        this.setID(id);
        this.setTitle(title);
        this.setDescription(desc);
        this.setLocation(loc);
        this.setType(type);
        this.setCustomerID(cuid);
        this.setUserID(uid);
        this.setContactID(coid);
        this.setStart(startTime.toLocalDateTime());           //converts the timestamp from the database to a localdatetime
        this.setEnd(endTime.toLocalDateTime());
    }
    /**Alternate constructor with LocalDateTimes. This constructor is primarily used when inputting from forms in the application.*/
    public Appointment(int id, String title, String desc, String loc, String type, LocalDateTime startTime, LocalDateTime endTime, int cuid, int uid, int coid){
        this.setID(id);
        this.setTitle(title);
        this.setDescription(desc);
        this.setLocation(loc);
        this.setType(type);
        this.setCustomerID(cuid);
        this.setUserID(uid);
        this.setContactID(coid);
        this.setStart(startTime);
        this.setEnd(endTime);
    }
    /**Checks whether this appointment overlaps with another. */
    public boolean isOverlapping(Appointment apt){
        if((start.isBefore(apt.getStart()) && end.isAfter(apt.getStart()))
           ||start.isBefore(apt.getEnd()) && end.isAfter(apt.getStart())){
            return true;
        }
        return false;
    }
    /**Updates this appointment in the SQL database. It will update whichever appointment shares its User_ID. */
    public void updateToDatabase() throws SQLException {       //adds this appointment to the database

        String sql = "UPDATE appointments SET Title = ?,Description = ?,Location = ?," +
                     "Type = ?,Start = ?,End = ?,Customer_ID = ?,User_ID = ?,Contact_ID = ? WHERE Appointment_ID = ?";

        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ps.setString(1,getTitle());
        ps.setString(2,getDescription());
        ps.setString(3,getLocation());
        ps.setString(4,getType());
        ps.setTimestamp(5,Timestamp.valueOf(getStart()));
        ps.setTimestamp(6,Timestamp.valueOf(getEnd()));
        ps.setInt(7,getCustomerID());
        ps.setInt(8,getUserID());
        ps.setInt(9,getContactID());
        ps.setInt(10,getID());
        ps.executeUpdate();
    }


    //getters and setters

    /**Returns this object's appointment ID.*/
    public int getID() {
        return ID;
    }
    /**Changes this object's appointment ID.*/
    public void setID(int ID) {
        this.ID = ID;
    }
    /**Returns this object's title. */
    public String getTitle() {
        return title;
    }
    /**Changes this object's title. */
    public void setTitle(String title) {
        this.title = title;
    }
    /**Returns this object's description. */
    public String getDescription() {
        return description;
    }
    /**Changes this object's description.*/
    public void setDescription(String description) {
        this.description = description;
    }
    /**Returns this object's location. */
    public String getLocation() {
        return location;
    }
    /**Changes this object's location.*/
    public void setLocation(String location) {
        this.location = location;
    }
    /**Returns this object's type. */
    public String getType() {
        return type;
    }
    /**Changes this object's type.*/
    public void setType(String type) {
        this.type = type;
    }
    /**Returns this object's start time. */
    public LocalDateTime getStart() {
        return start;
    }
    /**Changes this object's start date/time.*/
    public void setStart(LocalDateTime start) {
        this.start = start;
    }
    /**Returns this object's end time. */
    public LocalDateTime getEnd() {
        return end;
    }
    /**Changes this object's end date/time. */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }
    /**Returns this object's customer ID.*/
    public int getCustomerID() {
        return customerID;
    }
    /**Changes this object's customer ID. */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }
    /**Returns this object's user ID.*/
    public int getUserID() {
        return userID;
    }
    /**Changes this object's user ID. */
    public void setUserID(int userID) {
        this.userID = userID;
    }
    /**Changes this object's contact ID. */
    public int getContactID() {
        return contactID;
    }
    /**Changes this object's contact ID. */
    public void setContactID(int contactID) {
        this.contactID = contactID;
    }




}

package Controllers;

import POJOs.Appointment;
import POJOs.Contact;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;

/** This class is responsible for controlling the update appointments screen. */
public class UpdateApptController{ //this class is responsible for controlling the update appointments screen

    @FXML
    private Button cancelButton;
    @FXML
    private TextField idBox;
    @FXML
    private TextField titleBox;
    @FXML
    private TextField descBox;
    @FXML
    private TextField locBox;
    @FXML
    private ComboBox<Contact> contactBox;
    @FXML
    private TextField typeBox;
    @FXML
    private ComboBox<LocalTime> startTimeBox;
    @FXML
    private ComboBox<LocalTime> endTimeBox;
    @FXML
    private TextField customerIDBox;
    @FXML
    private TextField userIDBox;
    @FXML
    private Label errorText;
    @FXML
    private DatePicker dateBox;

    private int currentUser;
    private String userName;
    private Appointment targetAppt;
    /**Accepts values passed from other controllers and populates appointment data. Populates possible appointment hours based on the user's time zone.
     @param appt The appointment being updated*/
    public void setup(int id,String name, Appointment appt){        //accepts values passed from other controllers and sets up boxes
        currentUser = id;
        userName = name;
        targetAppt = appt;

        try {
            ObservableList<Contact> contacts = FXCollections.observableArrayList();
            String sql = "SELECT * FROM contacts";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while(rs.next()){       //makes a list of contacts
                Contact c = new Contact(rs.getInt("Contact_ID"),
                        rs.getString("Email"),
                        rs.getString("Contact_Name"));
                contacts.add(c);
            }
            contactBox.getItems().setAll(contacts);
            for(Contact a:contacts){                                 //finds the contact associated with the record
                if(a.getID() == targetAppt.getContactID()){
                    contactBox.setValue(a);
                    break;
                }
            }
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        typeBox.setText(targetAppt.getType());                      //setting up the input boxes with the previous data
        idBox.setText(Integer.toString(targetAppt.getID()));
        titleBox.setText(targetAppt.getTitle());
        locBox.setText(targetAppt.getLocation());
        descBox.setText(targetAppt.getDescription());
        customerIDBox.setText(Integer.toString(targetAppt.getCustomerID()));
        userIDBox.setText(Integer.toString(targetAppt.getUserID()));
        dateBox.setValue(targetAppt.getStart().toLocalDate());
        LocalTime businessStart = ZonedDateTime.of(1,1,1,8,0,0,0, ZoneId.of("EST", ZoneId.SHORT_IDS))
                .withZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
        LocalTime businessEnd = ZonedDateTime.of(1,1,1,22,0,0,0, ZoneId.of("EST", ZoneId.SHORT_IDS))
                .withZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
        ObservableList<LocalTime> businessHours = FXCollections.observableArrayList();
        for(int i = businessStart.getHour();i<=businessEnd.getHour();i++){              //populating a combo box with all valid appointment times
            businessHours.add(LocalTime.of(i,0));
            if(i<businessEnd.getHour()) {
                businessHours.add(LocalTime.of(i,15));
                businessHours.add(LocalTime.of(i, 30));
                businessHours.add(LocalTime.of(i,45));
            }
        }
        if(!businessHours.contains(targetAppt.getStart().toLocalTime())){           //allowing for appointments in the database not covered by 15 minute intervals
            businessHours.add(targetAppt.getStart().toLocalTime());
        }
        startTimeBox.getItems().setAll(businessHours);
        startTimeBox.setValue(targetAppt.getStart().toLocalTime());

        if(!businessHours.contains(targetAppt.getEnd().toLocalTime())){
            businessHours.add(targetAppt.getEnd().toLocalTime());
        }
        endTimeBox.getItems().setAll(businessHours);
        endTimeBox.setValue(targetAppt.getEnd().toLocalTime());
    }
    /**This method returns the user to the appointments screen. */
    public void backToAppointments() throws IOException {       //closes the current window and re-opens the appointment window
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/Appointments.fxml"));
        Parent root = loader.load();
        AppointmentsController apptController = loader.getController();
        apptController.setup(currentUser,userName);
        newStage.setScene(new Scene(root));
        stage.close();
        newStage.show();
    }
    /**This method checks the input forms for invalid inputs, then updates the appointment and returns the user to the appointments menu. */
    public void updateAppt() throws IOException{           //checks for invalid inputs, then updates the appointment and returns to the appointment menu
        String sql;
        PreparedStatement ps;
        ResultSet rs;
        Boolean isValid = false;
        int targetID = -1;


        if(startTimeBox.getValue() == null){
            errorText.setText("Please choose a starting time");
            errorText.setVisible(true);
            return;
        }
        if(endTimeBox.getValue() == null){
            errorText.setText("Please choose an ending time");
            errorText.setVisible(true);
            return;
        }

        if(contactBox.getValue() == null){
            errorText.setText("Please choose a contact");
            errorText.setVisible(true);
            return;
        }

        if(dateBox.getValue().getDayOfWeek() == DayOfWeek.SATURDAY ||
                dateBox.getValue().getDayOfWeek() == DayOfWeek.SUNDAY){
            errorText.setText("Please choose an appointment within business hours");
            errorText.setVisible(true);
            return;
        }

        if(!startTimeBox.getValue().isBefore(endTimeBox.getValue())){
            errorText.setText("The appointment end time must be after the start time");
            errorText.setVisible(true);
            return;
        }

        if(titleBox.getText().isBlank()){
            errorText.setText("Please enter a title");
            errorText.setVisible(true);
            return;
        }

        if(descBox.getText().isBlank()){
            errorText.setText("Please enter a description");
            errorText.setVisible(true);
            return;
        }

        if(locBox.getText().isBlank()){
            errorText.setText("Please enter a location");
            errorText.setVisible(true);
            return;
        }

        if(typeBox.getText().isBlank()){
            errorText.setText("Please enter a type");
            errorText.setVisible(true);
            return;
        }

        try {
            Integer.parseInt(customerIDBox.getText());
        }catch(NumberFormatException e){
            errorText.setText("Please enter a valid customer ID");
            errorText.setVisible(true);
            return;
        }
        try{
            Integer.parseInt(userIDBox.getText());
        }catch(NumberFormatException e){
            errorText.setText("Please enter a valid user ID");
            errorText.setVisible(true);
            return;
        }


        try{
            sql = "SELECT * FROM customers";
            ps = JDBC.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("Customer_ID") == Integer.parseInt(customerIDBox.getText())){
                    isValid = true;
                    targetID = rs.getInt("Customer_ID");
                    break;
                }
            }
            if(!isValid){
                errorText.setText("Please enter a valid customer ID");
                errorText.setVisible(true);
                return;
            }



            isValid = false;
            sql = "SELECT * FROM users";
            ps = JDBC.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("User_ID") == Integer.parseInt(userIDBox.getText())){
                    isValid = true;
                    break;
                }
            }
            if(!isValid){
                errorText.setText("Please enter a valid user ID");
                errorText.setVisible(true);
                return;
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        targetAppt.setTitle(titleBox.getText());
        targetAppt.setDescription(descBox.getText());
        targetAppt.setLocation(locBox.getText());
        targetAppt.setType(typeBox.getText());
        targetAppt.setStart(LocalDateTime.of(dateBox.getValue(),startTimeBox.getValue()));
        targetAppt.setEnd(LocalDateTime.of(dateBox.getValue(),endTimeBox.getValue()));
        targetAppt.setCustomerID(Integer.parseInt(customerIDBox.getText()));
        targetAppt.setUserID(Integer.parseInt(userIDBox.getText()));
        targetAppt.setContactID(contactBox.getValue().getID());
        try{
            sql = "SELECT * FROM appointments WHERE Customer_ID = ?";
            ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1,targetID);
            rs = ps.executeQuery();
            while(rs.next()){
                Appointment a = new Appointment(rs.getInt("Appointment_ID"),
                                                rs.getString("Title"),
                                                rs.getString("Description"),
                                                rs.getString("Location"),
                                                rs.getString("Type"),
                                                rs.getTimestamp("Start"),
                                                rs.getTimestamp("End"),
                                                rs.getInt("Customer_ID"),
                                                rs.getInt("User_ID"),
                                                rs.getInt("Contact_ID"));
                if(a.isOverlapping(targetAppt) && (targetAppt.getID() != a.getID())){
                    errorText.setText("Customer already has an appointment at that time");
                    errorText.setVisible(true);
                    return;
                }
            }
            targetAppt.updateToDatabase();
        }catch(SQLException e){
            e.printStackTrace();
        }
        backToAppointments();
    }


}


package Controllers;

import POJOs.Appointment;
import POJOs.Contact;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.util.ResourceBundle;
/**This class is responsible for controlling the Add Appointments screen. It adds a preparatory appointment to the database when it is created, then updates the information if the add is confirmed, or deletes the appointment if the operation is cancelled. */
public class AddApptController implements Initializable {       //this class is responsible for controlling the Add Appointments screen

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
    private int apptID;

    /**This method accepts values passed from other controllers. */
    public void setup(int id,String name){
        currentUser = id;
        userName = name;
    }
    /**This method runs when the object is created. It prepares the combo boxes' contents, and adds a preparatory appointment to the database in order to get the generated ID to populate the ID box. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<Contact> contacts = FXCollections.observableArrayList();
            String sql = "INSERT INTO appointments(Title,Description,Location,Type,Start,End,Customer_ID,User_ID,Contact_ID) " +
                         "VALUES('default','default','default','default',?,?,2,2,2)";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setTimestamp(1,Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(2,Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            apptID = rs.getInt(1);
            idBox.setText(Integer.toString(apptID));

            sql = "SELECT * FROM contacts";
            ps = JDBC.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                Contact c = new Contact(rs.getInt("Contact_ID"),
                                          rs.getString("Email"),
                                          rs.getString("Contact_Name"));
                contacts.add(c);
            }
            contactBox.getItems().setAll(contacts);
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        dateBox.setValue(LocalDateTime.now().toLocalDate());
        LocalTime businessStart = ZonedDateTime.of(1,1,1,8,0,0,0, ZoneId.of("EST", ZoneId.SHORT_IDS))
                                  .withZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
        LocalTime businessEnd = ZonedDateTime.of(1,1,1,22,0,0,0, ZoneId.of("EST", ZoneId.SHORT_IDS))
                                .withZoneSameInstant(ZoneId.systemDefault()).toLocalTime();
        ObservableList<LocalTime> businessHours = FXCollections.observableArrayList();
        for(int i = businessStart.getHour();i<=businessEnd.getHour();i++){
            businessHours.add(LocalTime.of(i,0));
            if(i<businessEnd.getHour()) {
                businessHours.add(LocalTime.of(i,15));
                businessHours.add(LocalTime.of(i, 30));
                businessHours.add(LocalTime.of(i,45));
            }
        }
        startTimeBox.getItems().setAll(businessHours);
        endTimeBox.getItems().setAll(businessHours);

    }
    /**This method deletes the prospective appointment from the database and returns to the appointments menu. */
    public void cancelAdd() throws IOException{
        try{
            String sql = "DELETE FROM appointments WHERE Appointment_ID = ?";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1,apptID);
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        backToAppointments();
    }
    /**This method closes the current window and re-opens the appointment window. */
    public void backToAppointments() throws IOException {       //


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
    /**This method checks the input form for errors, then puts the information into the database. A different error message will display for each potential invalidity. */
    public void addAppt() throws IOException{

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

        String sql;
        PreparedStatement ps;
        ResultSet rs;
        Boolean isValid = false;
        try{
            sql = "SELECT * FROM customers";
            ps = JDBC.getConnection().prepareStatement(sql);
            rs = ps.executeQuery();
            while(rs.next()){
                if(rs.getInt("Customer_ID") == Integer.parseInt(customerIDBox.getText())){
                    isValid = true;
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

        Appointment a = new Appointment(apptID,                                             //creates the appointment to be sent to the database
                                        titleBox.getText(),
                                        descBox.getText(),
                                        locBox.getText(),
                                        typeBox.getText(),
                                        LocalDateTime.of(dateBox.getValue(),startTimeBox.getValue()),
                                        LocalDateTime.of(dateBox.getValue(),endTimeBox.getValue()),
                                        Integer.parseInt(customerIDBox.getText()),
                                        Integer.parseInt(userIDBox.getText()),
                                        contactBox.getValue().getID());
        try{
            sql = "SELECT * FROM appointments WHERE Customer_ID = ?";
            ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1,Integer.parseInt(idBox.getText()));
            rs = ps.executeQuery();
            while(rs.next()){                                                               //checks for overlapping appointments
                Appointment b = new Appointment(rs.getInt("Appointment_ID"),
                                                rs.getString("Title"),
                                                rs.getString("Description"),
                                                rs.getString("Location"),
                                                rs.getString("Type"),
                                                rs.getTimestamp("Start"),
                                                rs.getTimestamp("End"),
                                                rs.getInt("Customer_ID"),
                                                rs.getInt("User_ID"),
                                                rs.getInt("Contact_ID"));
                if(a.isOverlapping(b) && a.getID() != b.getID()){
                    errorText.setText("Customer already has an appointment at that time");
                    errorText.setVisible(true);
                    return;
                }
            }
            a.updateToDatabase();
        }catch(SQLException e){
            e.printStackTrace();
        }
        backToAppointments();                                                           //returns to the previous window
    }


}


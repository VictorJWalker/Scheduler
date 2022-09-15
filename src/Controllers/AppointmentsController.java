package Controllers;

import POJOs.Appointment;
import POJOs.MonthResults;
import POJOs.QueryResults;
import POJOs.TypeResults;
import helper.JDBC;
import helper.displayInterface;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.management.Query;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
/**This class is responsible for controlling the Appointments screen. */
public class AppointmentsController implements Initializable { //this class is responsible for controlling the Appointments screen

    @FXML
    private Button addAppt;
    @FXML
    private Button updateAppt;
    @FXML
    private Button backToMenu;
    @FXML
    private TableView<Appointment> apptTable;
    @FXML
    private TableColumn<Appointment, String> apptID;
    @FXML
    private TableColumn<Appointment, String> title;
    @FXML
    private TableColumn<Appointment, String> desc;
    @FXML
    private TableColumn<Appointment, String> loc;
    @FXML
    private TableColumn<Appointment, String> type;
    @FXML
    private TableColumn<Appointment, String> start;
    @FXML
    private TableColumn<Appointment, String> end;
    @FXML
    private TableColumn<Appointment, String> customerID;
    @FXML
    private TableColumn<Appointment, String> userID;
    @FXML
    private TableColumn<Appointment, String> contactID;
    @FXML
    private RadioButton monthView;
    @FXML
    private RadioButton weekView;
    @FXML
    private RadioButton allView;
    @FXML
    private Label notiLabel;
    @FXML
    private Button delYes;
    @FXML
    private Button delNo;
    @FXML
    private Label delConfirm;
    @FXML
    private Label updateNoti;
    @FXML
    private TableView<QueryResults> totalTable;
    @FXML
    private TableColumn<QueryResults,String> totalCategory;
    @FXML
    private TableColumn<QueryResults,String> totalAmount;
    @FXML
    private Button toggleView;
    @FXML
    private Button deleteAppt;

    private ObservableList<TypeResults> typeList;
    private ObservableList<MonthResults> monthList;
    private ObservableList<Appointment> apptList;
    private int currentUser;
    private String userName;
    private Appointment selectedAppt;
    private Boolean inTotalMode;



    /**This method runs when the object is created. It initializes a few variables, then populates the appointment table. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {    //populates the appointment table and clears any error messages
        inTotalMode = false;
        apptList = FXCollections.observableArrayList();
        try {
            updateAppointments();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        notiLabel.setVisible(false);
    }
    /**This method returns the user to the main menu. */
    public void backToMenu() throws IOException {       //closes the appointment window and returns to the main menu
        Stage stage = (Stage)backToMenu.getScene().getWindow();
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/MainMenu.fxml"));
        Parent root = loader.load();
        MainMenuController c = loader.getController();
        c.setup(currentUser,userName);
        newStage.setScene(new Scene(root));
        stage.close();
        newStage.show();
    }
    /**This method confirms an appointment is selected, then prompts the user to confirm deletion. */
    public void delPrompt(){
        if(!apptTable.getSelectionModel().isEmpty()) {
            selectedAppt = apptTable.getSelectionModel().getSelectedItem();
            delConfirm.setText("Are you sure you want to delete this appointment?");
            delYes.setVisible(true);
            delNo.setVisible(true);
        }
        else{
            delConfirm.setText("No appointment selected.");
        }
        delConfirm.setVisible(true);
    }
    /**This method deletes an appointment. A lambda expression is used here in order to convert the appointment object that was deleted into a string in order to display its details in the deletion confirmation message. */
    public void deleteAppt() throws SQLException{   //deletes selected appointment and displays confirmation message
        String sql =  "DELETE FROM appointments WHERE Appointment_ID = ?";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ps.setInt(1,selectedAppt.getID());
        ps.executeUpdate();
        updateAppointments();
        clearNotis();
        //This interface will convert an appointment into a string
        //The lambda specifies that in this case we want the appointment ID and the type
        displayInterface display = a->"Appointment "+a.getID()+" of type "+a.getType();
        //Here we use the display interface to display the relevant appointment details
        delConfirm.setText(display.displayDetails(selectedAppt)+" has been deleted.");
        delConfirm.setVisible(true);
    }
    /**This method changes the displayed table. If the user is viewing the list of appointments, it displays this month's appointments. If the user is viewing the total appointment counts, it displays the number of appointments in each month. */
    public void setMonthView() throws SQLException{ //changes the view to display this month's appointments
        monthView.setSelected(true);
        allView.setSelected(false);
        weekView.setSelected(false);
        if(!inTotalMode){
            updateAppointments();
        }
        else{
            updateTotals();
            displayMonthQuery();
        }
        clearNotis();
    }
    /**This method changes the appointment table to display appointments occuring in the current week. */
    public void setWeekView() throws SQLException{ //changes the view to display this week's appointments

        monthView.setSelected(false);
        allView.setSelected(false);
        weekView.setSelected(true);
        updateAppointments();
        clearNotis();
    }
    /**This method changes the displayed table. If the user is viewing the list of appointments, it displays all appointments. If the user is viewing the total appointment counts, it displays the number of appointments of each type. */
    public void setAllView() throws SQLException{  //changes the view to display all appointments or type query

        monthView.setSelected(false);
        allView.setSelected(true);
        weekView.setSelected(false);
        if(!inTotalMode){
            updateAppointments();
        }
        else{
            updateTotals();
            displayTypeQuery();
        }
        clearNotis();
    }
    /**This method opens the add appointment menu. */
    public void addAppt() throws IOException{           //opens the window for adding appointments
        Stage stage = (Stage)addAppt.getScene().getWindow();
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/addAppt.fxml"));
        Parent root = loader.load();
        AddApptController c = loader.getController();
        c.setup(currentUser,userName);
        newStage.setScene(new Scene(root));
        stage.close();
        newStage.show();
    }
    /**This method opens the update appointment menu. If no appointment is selected, it will instead display an error message. */
    public void updateAppt() throws IOException{                //opens the update appointment window
        if(apptTable.getSelectionModel().getSelectedItem()!= null) {
            Stage stage = (Stage) updateAppt.getScene().getWindow();
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/updateAppt.fxml"));
            Parent root = loader.load();
            UpdateApptController c = loader.getController();
            c.setup(currentUser, userName, apptTable.getSelectionModel().getSelectedItem());
            newStage.setScene(new Scene(root));
            stage.close();
            newStage.show();
        }
        else{
            updateNoti.setText("Please select an item.");
            updateNoti.setVisible(true);
        }
    }
    /**This method changes the total appointments table to display appointments sorted by month. */
    public void displayMonthQuery(){                //displays appointments by month
        totalTable.getItems().setAll(monthList);
        totalAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        totalCategory.setCellValueFactory(new PropertyValueFactory<>("month"));
    }
    /**This method changes the total appointments table to display the appointments sorted by type. */
    public void displayTypeQuery(){             //displays appointments by type
        totalTable.getItems().setAll(typeList);
        totalAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        totalCategory.setCellValueFactory(new PropertyValueFactory<>("type"));
    }

    /**This method updates the total appointments table. It selects all appointments from the database, then counts how many appointments fall under each type and month. */
    public void updateTotals() throws SQLException{         //runs a query for total appointments
        String sql = "SELECT * FROM appointments";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        Boolean typePresent = false;
        typeList = FXCollections.observableArrayList();
        monthList = FXCollections.observableArrayList();
        while(rs.next()){
            for(TypeResults t:typeList){
                typePresent = false;
                if(t.getType().equalsIgnoreCase(rs.getString("Type"))){
                    t.increment();
                    typePresent = true;
                    break;
                }
            }
            if(!typePresent) {
                TypeResults type = new TypeResults(rs.getString("Type"));
                typeList.add(type);
            }
            for(MonthResults m:monthList){
                typePresent = false;
                if(m.getMonth() == rs.getTimestamp("Start").toLocalDateTime().getMonth()){
                    m.increment();
                    typePresent = true;
                    break;
                }
            }
            if(!typePresent) { MonthResults month = new MonthResults(rs.getTimestamp("Start").toLocalDateTime().getMonth());
                monthList.add(month);
            }
        }
    }
    /**This method clears all error messages and notifications. */
    public void clearNotis(){     //removes the prompt to delete appointment and clears the selected appointment
        delYes.setVisible(false);
        delNo.setVisible(false);
        delConfirm.setVisible(false);
        notiLabel.setVisible(false);
    }
    /**This method changes from the total appointment view to the appointment list view, and vice versa. */
    public void toggleView() throws SQLException{       //toggles between appointment total mode and regular appointment mode
        if(!inTotalMode) {
            clearNotis();
            apptTable.setVisible(false);
            updateTotals();
            weekView.setVisible(false);
            weekView.setSelected(false);
            allView.setSelected(true);
            allView.setText("Type");
            displayTypeQuery();
            monthView.setSelected(false);
            totalTable.setVisible(true);
            updateAppt.setVisible(false);
            addAppt.setVisible(false);
            deleteAppt.setVisible(false);
            toggleView.setText("View appointment list");
            inTotalMode = true;
        }
        else{
            clearNotis();
            totalTable.setVisible(false);
            apptTable.setVisible(true);
            weekView.setVisible(true);
            weekView.setSelected(false);
            allView.setSelected(true);
            allView.setText("All");
            monthView.setSelected(false);
            updateAppt.setVisible(true);
            addAppt.setVisible(true);
            deleteAppt.setVisible(true);
            toggleView.setText("View appointment totals");
            inTotalMode = false;
        }

    }
    /**This method updates the appointments table based on which view is selected. */
    public void updateAppointments() throws SQLException {          //runs through the database and updates the appointment table
        String sql;
        PreparedStatement ps;
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();

;        if (monthView.isSelected()) {
            sql = "SELECT * FROM appointments WHERE Start BETWEEN ? AND ?";
            ps = JDBC.getConnection().prepareStatement(sql);
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.of(today.minusDays(today.getDayOfMonth()-1),LocalTime.of(0,0))));                          //start of the month
            ps.setTimestamp(2,Timestamp.valueOf(LocalDateTime.of(today.minusDays(today.getDayOfMonth()-1).plusMonths(1),LocalTime.of(0,0))));             //start of the next month
        } else if (weekView.isSelected()) {
            sql = "SELECT * FROM appointments WHERE Start BETWEEN ? AND ?";
            ps = JDBC.getConnection().prepareStatement(sql);
            ps.setTimestamp(1,Timestamp.valueOf(LocalDateTime.of(today.minusDays(today.getDayOfWeek().getValue() - 1),LocalTime.of(0,0))));               //start of the week
            ps.setTimestamp(2,Timestamp.valueOf(LocalDateTime.of(today.minusDays(today.getDayOfWeek().getValue() - 1).plusWeeks(1),LocalTime.of(0,0))));  //start of the next week
        } else {
            sql = "SELECT * FROM appointments";
            ps = JDBC.getConnection().prepareStatement(sql);
        }

        ResultSet rs = ps.executeQuery();
        if(apptList != null){
            apptList.clear();
        }
        while(rs.next()){                   //adds each appointment to the list of appointments
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
            apptList.add(a);
        }
        apptTable.getItems().setAll(apptList);                                                             //attaches the list of appointments to the appointment table
        apptID.setCellValueFactory(new PropertyValueFactory<Appointment, String>("ID"));                //assigning variables to each table column
        title.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        desc.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        loc.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        type.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        start.setCellValueFactory(new PropertyValueFactory<Appointment,String>("start"));
        end.setCellValueFactory(new PropertyValueFactory<Appointment,String>("end"));
        userID.setCellValueFactory(new PropertyValueFactory<Appointment, String>("userID"));
        contactID.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactID"));
        customerID.setCellValueFactory(new PropertyValueFactory<Appointment, String>("customerID"));

    }
    /**This method accepts values passed from other classes. */
    public void setup(int id, String name){     //accepts values passed from other classes
        currentUser = id;
        userName = name;
    }

}

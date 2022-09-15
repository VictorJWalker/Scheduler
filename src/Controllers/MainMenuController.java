package Controllers;

import POJOs.Appointment;
import helper.JDBC;
import helper.displayInterface;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.sql.*;
/**This class is responsible for controlling the main menu. */
public class MainMenuController{ //this class is responsible for controlling the main menu

    private int currentUser;
    private String userName;

    @FXML
    private Label apptNoti;
    @FXML
    private Label welcomeLabel;
    @FXML
    private Button apptButton;
    @FXML
    private Button customerButton;
    @FXML
    private Label aptDetails;
    @FXML
    private Label apptCount;

    /**This method checks for appointments in the next 15 minutes and displays the number of appointments scheduled for the day.
       A lambda expression is used in this method in order to display the information of an appointment scheduled in the next 15 minutes. */
    public void apptCheck() throws SQLException{                        //checks to see if there are any appointments in the next 15 minutes and counts appointments today
        int count = 0;
        String sql = "SELECT * FROM appointments WHERE User_ID = ?";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ps.setInt(1,currentUser);

        //This interface will convert an appointment into a string
        //The lambda specifies that in this case we want the appointment ID, the location, and the time
        displayInterface display = a -> "Appointment details- ID: "+a.getID()+ " | Location: "+a.getLocation()
                                         +" | Time: "+a.getStart().toString();

        try{
            ResultSet rs = ps.executeQuery();
            apptNoti.setText("You have no appointments starting in the next 15 minutes.");
            while(rs.next()){                                           //checks for appointments in the next 15 minutes
                if(rs.getTimestamp("Start").toLocalDateTime().isAfter(LocalDateTime.now()) &&
                  rs.getTimestamp("Start").toLocalDateTime().isBefore(LocalDateTime.now().plusMinutes(15))){
                    apptNoti.setText("You have an appointment starting in the next 15 minutes.");
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
                    //using the display details interface here to display the appointment info
                    aptDetails.setText(display.displayDetails(a));
                    aptDetails.setVisible(true);
                    break;
                }
            }
            sql = "SELECT * FROM appointments WHERE Start BETWEEN ? and ?";     //checks for appointments scheduled today
            ps = JDBC.getConnection().prepareStatement(sql);
            ps.setTimestamp(1,Timestamp.valueOf(LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0))));
            ps.setTimestamp(2,Timestamp.valueOf(LocalDateTime.of(LocalDate.now().plusDays(1),LocalTime.of(0,0,0))));
            rs = ps.executeQuery();
            while(rs.next()){
                count++;
            }
            if(count == 1) {
                apptCount.setText("There is 1 appointment scheduled for today.");
            }else if(count == 0){
                apptCount.setText("There are no appointments scheduled for today.");
            }else{
                apptCount.setText("There are "+count+" appointments scheduled for today.");
            }

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    /**This method accepts data passed from other controllers. */
    public void setup(int user, String name){               //accepts data passed from other classes and checks for coming appointments
        currentUser = user;
        userName = name;
        welcomeLabel.setText("Welcome, " + userName);
        try {
            apptCheck();
        } catch (SQLException throwables) {
            System.out.println("Error in retrieving appointment data");
        }
    }
    /**This method opens the Customers screen. */
    public void openCustomers() throws Exception{        //opens the customers window
        Stage stage = (Stage) customerButton.getScene().getWindow();
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/Customers.fxml"));
        Parent root = loader.load();
        CustomerController customerController = loader.getController();
        customerController.setup(currentUser,userName);
        newStage.setScene(new Scene(root));
        stage.close();
        newStage.show();
    }
    /**This method opens the Appointments screen. */
    public void openAppointments() throws Exception{     //opens the appointments window
        Stage stage = (Stage) apptButton.getScene().getWindow();
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/Appointments.fxml"));
        Parent root = loader.load();
        AppointmentsController apptController = loader.getController();
        apptController.setup(currentUser,userName);
        newStage.setScene(new Scene(root));
        stage.close();
        newStage.show();

    }
}

package Controllers;

import POJOs.Appointment;
import POJOs.Customer;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**This class is responsible for controlling the schedule viewer screen. */

public class ViewScheduleController { //this class is responsible for controlling the view schedule screen
    @FXML
    private Label titleLabel;
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
    private Button backToMenu;

    private Customer targetCust;
    private int currentUser;
    private String userName;
    private ObservableList<Appointment> apptList;

    /**This method accepts values from other classes, then sets the title label and populates the table.
      @param c The customer whose schedule is being displayed. */

    public void setup(int id, String name, Customer c) {        //this method accepts values from other classes, then sets the title label and initializes the table
        targetCust = c;
        currentUser = id;
        userName = name;
        apptList = FXCollections.observableArrayList();
        titleLabel.setText("Schedule for " + targetCust.getName());
        try {
            updateAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**This method populates the appointments table. */
    public void updateAppointments() throws SQLException {          //runs through the database and updates the appointment table

        String sql = "SELECT * FROM appointments WHERE Customer_ID = ?";
        PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
        ps.setInt(1, targetCust.getCustomerID());
        ResultSet rs = ps.executeQuery();
        apptList.clear();
        while (rs.next()) {                   //adds each appointment to the list of appointments
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
        start.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startDisplay"));
        end.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endDisplay"));
        userID.setCellValueFactory(new PropertyValueFactory<Appointment, String>("userID"));
        contactID.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactID"));
        customerID.setCellValueFactory(new PropertyValueFactory<Appointment, String>("customerID"));
    }
    /**This method returns the user to the main menu.*/
    public void backToMenu() throws IOException {               //returns to the customers window
            Stage stage = (Stage) backToMenu.getScene().getWindow();
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/Customers.fxml"));
            Parent root = loader.load();
            CustomerController customerController = loader.getController();
            customerController.setup(currentUser,userName);
            newStage.setScene(new Scene(root));
            stage.close();
            newStage.show();
    }

}


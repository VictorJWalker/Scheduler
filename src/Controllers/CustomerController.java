package Controllers;

import POJOs.Appointment;
import POJOs.Country;
import POJOs.Customer;
import POJOs.Division;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;
/**This class is responsible for controlling the Customers screen. */
public class CustomerController implements Initializable{       //this class is responsible for controlling the Customers screen

    @FXML
    private TableView<Customer> custTable;
    @FXML
    private TableColumn<Customer, String> IDCol;
    @FXML
    private TableColumn<Customer, String> nameCol;
    @FXML
    private TableColumn<Customer, String> addressCol;
    @FXML
    private TableColumn<Customer, String> zipCol;
    @FXML
    private TableColumn<Customer, String> phoneCol;
    @FXML
    private TableColumn<Customer,String> divCol;
    @FXML
    private TableColumn<Customer,String> countryCol;
    @FXML
    private Button addCust;
    @FXML
    private Button updateCust;
    @FXML
    private Button backToMenu;
    @FXML
    private Button delYes;
    @FXML
    private Button delNo;
    @FXML
    private Label delNoti;
    @FXML
    private Label updateNoti;
    @FXML
    private Label schedNoti;
    @FXML
    private Button viewSchedule;

    private int currentUser;
    private String userName;
    private Customer selectedCust;
    private ObservableList<Customer> customerList;

    /**This method runs when the object is created. It initializes a few variables, then populates the customer table. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {                //populates the list of customers
        customerList = FXCollections.observableArrayList();
        try{
            updateCustomers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**This method clears all error messages and notifications. */
    public void clearNotis(){                                   //clears any notifications on the screen
        delNoti.setVisible(false);
        updateNoti.setVisible(false);
        delYes.setVisible(false);
        delNo.setVisible(false);
    }
    /**This method confirms an appointment is selected, then prompts the user to confirm deletion. */
    public void delPrompt(){
        clearNotis();
        selectedCust = custTable.getSelectionModel().getSelectedItem();
        if(selectedCust != null) {
            delYes.setVisible(true);
            delNo.setVisible(true);
            delNoti.setText("Are you sure you want to delete this customer?");
        }
        else{
            delNoti.setText("Please choose a customer to delete.");
        }
        delNoti.setVisible(true);
    }
    /**This method deletes a customer from the database, then updates the table and displays a confirmation. */
    public void deleteCust(){           //deletes a customer's appointments and then the customer
        clearNotis();
        try{
            String sql = "DELETE FROM appointments WHERE Customer_ID = ?";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1,selectedCust.getCustomerID());
            ps.executeUpdate();
            sql = "DELETE FROM customers WHERE Customer_ID = ?";
            ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1,selectedCust.getCustomerID());
            ps.executeUpdate();
            updateCustomers();
        }catch(SQLException e){
            e.printStackTrace();
        }
        delNoti.setText("Customer "+selectedCust.getCustomerID()+", "+selectedCust.getName()+" has been deleted.");
        delNoti.setVisible(true);

    }
    /**This method takes the user back to the main menu. */
    public void backToMenu() throws IOException{                //returns to the main menu
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
    /**This method opens the add customer menu. */
    public void addCust() throws IOException {                  //opens the add customer menu
        Stage stage = (Stage)addCust.getScene().getWindow();
        Stage newStage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/addCustomer.fxml"));
        Parent root = loader.load();
        AddCustomerController c = loader.getController();
        c.setup(currentUser,userName);
        newStage.setScene(new Scene(root));
        stage.close();
        newStage.show();
    }
    /**This method confirms a customer is selected, then opens the update customer menu. */
    public void updateCust() throws IOException{                //opens the update customer menu
        clearNotis();
        if(custTable.getSelectionModel().getSelectedItem() != null) {
            Stage stage = (Stage) updateCust.getScene().getWindow();
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/updateCustomer.fxml"));
            Parent root = loader.load();
            UpdateCustomerController c = loader.getController();
            c.setup(currentUser, userName, custTable.getSelectionModel().getSelectedItem());
            newStage.setScene(new Scene(root));
            stage.close();
            newStage.show();
        }
        else{
            updateNoti.setText("Please select a customer to update");
            updateNoti.setVisible(true);
        }
    }
    /**This method opens the window to view a given customer's schedule. If no customer is selected, an error message is displayed. */
    public void viewSchedule() throws IOException {
        clearNotis();
        if(custTable.getSelectionModel().getSelectedItem() != null) {
            Stage stage = (Stage) viewSchedule.getScene().getWindow();
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/viewSchedule.fxml"));
            Parent root = loader.load();
            ViewScheduleController c = loader.getController();
            c.setup(currentUser, userName, custTable.getSelectionModel().getSelectedItem());
            newStage.setScene(new Scene(root));
            stage.close();
            newStage.show();
        }
        else{
            schedNoti.setText("Please select a customer");
            schedNoti.setVisible(true);
        }
    }
    /**This method queries the database and populates the customer table. */
    public void updateCustomers() throws SQLException {          //runs through the database and updates the customer table
        String sql;
        PreparedStatement ps;

        if(customerList != null){
            customerList.clear();
        }

        sql = "SELECT * FROM customers";
        ps = JDBC.getConnection().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        String sql2 = "SELECT * FROM first_level_divisions WHERE Division_ID = ?";
        PreparedStatement ps2 = JDBC.getConnection().prepareStatement(sql2);
        ResultSet rs2;
        while(rs.next()){                                                               //adds each customer to the list of customers
            ps2.setInt(1,rs.getInt("Division_ID"));
            rs2 = ps2.executeQuery();
            rs2.next();
            Customer c = new Customer(rs.getInt("Customer_ID"),
                                      rs.getString("Customer_Name"),
                                      rs.getString("Address"),
                                      rs.getString("Postal_Code"),
                                      rs.getString("Phone"),
                                      rs.getInt("Division_ID"),
                                      rs2.getInt("Country_ID"));
            customerList.add(c);
        }
        custTable.getItems().setAll(customerList);                                                             //attaches the list of customers to the table
        IDCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("customerID"));                    //assigning variables to each table column
        nameCol.setCellValueFactory(new PropertyValueFactory<Customer,String>("name"));
        divCol.setCellValueFactory(new PropertyValueFactory<Customer,String>("divID"));
        addressCol.setCellValueFactory(new PropertyValueFactory<Customer,String>("address"));
        zipCol.setCellValueFactory(new PropertyValueFactory<Customer,String>("zip"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<Customer,String>("phone"));
        countryCol.setCellValueFactory(new PropertyValueFactory<Customer,String>("countryID"));

    }
    /**This method accepts data from other controllers. */
    public void setup(int id, String name){         //accepts data from a previous window
        currentUser = id;
        userName = name;
    }
}

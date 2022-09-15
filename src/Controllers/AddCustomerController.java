package Controllers;

import POJOs.Country;
import POJOs.Customer;
import POJOs.Division;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**This class is responsible for controlling the Add Customer screen. It adds a preparatory customer to the database when it is created, then updates the information if the add is confirmed, or deletes the appointment if the operation is cancelled. */
public class AddCustomerController implements Initializable {       //this class is responsible for controlling the Add Customer screen

    @FXML
    private ComboBox<Division> divBox;
    @FXML
    private ComboBox<Country> countryBox;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField idBox;
    @FXML
    private TextField addressBox;
    @FXML
    private TextField zipBox;
    @FXML
    private TextField phoneBox;
    @FXML
    private Label errorText;
    @FXML
    private TextField nameBox;


    private ObservableList<Country> countryList;
    private ObservableList<Division> divList;
    private int currentUser;
    private String userName;
    private int custID;

    /**This method runs when the object is created. It populates the list of countries, initializes the list of divisions, and adds the preparatory customer to the database to get the generated ID. */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {            //initializes the division list and populates the country list
        countryList = FXCollections.observableArrayList();
        divList = FXCollections.observableArrayList();

        try{
            String sql = "SELECT * FROM countries";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Country newCountry = new Country(rs.getInt("Country_ID"), rs.getString("Country"));
                countryList.add(newCountry);
            }
            countryBox.setItems(countryList);
            sql = "INSERT INTO customers(Customer_Name,Address,Postal_Code,Phone,Division_ID) VALUES (default,default,default,default,1)";
            ps = JDBC.getConnection().prepareStatement(sql,PreparedStatement.RETURN_GENERATED_KEYS);
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            rs.next();
            custID = rs.getInt(1);
            idBox.setText(Integer.toString(custID));

        }catch(SQLException e){
            e.printStackTrace();
        }
    }
    /**This method accepts values from other controllers. */
    public void setup(int user, String name){
        currentUser = user;
        userName = name;
    }
    /**This method hecks the input form for errors, then puts the information into the database. A different error message will display for each potential invalidity. */
    public void addCustomer(){                      //tests inputs for validity, then updates trial entry to the actual values and returns to the customer table
        if(nameBox.getText().isBlank()){
            errorText.setText("Please input a name");
            errorText.setVisible(true);
            return;
        }
        if(addressBox.getText().isBlank()){
            errorText.setText("Please input an address");
            errorText.setVisible(true);
            return;
        }
        if(zipBox.getText().isBlank()){
            errorText.setText("Please input a postal code");
            errorText.setVisible(true);
            return;
        }
        if(phoneBox.getText().isBlank()){
            errorText.setText("Please input a phone number");
            errorText.setVisible(true);
            return;
        }
        if(countryBox.getValue() == null){
            errorText.setText("Please select a country");
            errorText.setVisible(true);
            return;
        }
        if(divBox.getValue() ==  null){
            errorText.setText("Please select a region");
            errorText.setVisible(true);
            return;
        }

        Customer toAdd = new Customer(custID,                       //creates the customer to add
                                      nameBox.getText(),
                                      addressBox.getText(),
                                      zipBox.getText(),
                                      phoneBox.getText(),
                                      divBox.getSelectionModel().getSelectedItem().getDivisionID(),
                                      divBox.getSelectionModel().getSelectedItem().getCountryID());
        try{
            toAdd.updateToDatabase();
        }catch(SQLException e){
            e.printStackTrace();
        }
        try {
            backToCustomers();
        }catch(IOException e){}
    }
    /**This method deletes the prospective customer from the database and returns to the customers menu. */
    public void cancelAdd() throws IOException{
        try{
            String sql = "DELETE FROM customers WHERE Customer_ID = ?";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1,custID);
            ps.executeUpdate();
        }catch(SQLException e){
            e.printStackTrace();
        }
        backToCustomers();
    }

    /**This method updates the contents of the division box based on the current value of the Country box. It is called whenever the Country box is changed.*/
    public void updateDivisions() throws SQLException {
        if (countryBox.getValue() != null) {
            if (!divList.isEmpty()) {
                divList.clear();
            }
            String sql = "SELECT * FROM first_level_divisions WHERE Country_ID = ?";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ps.setInt(1, countryBox.getValue().getCountryID());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Division d = new Division(rs.getInt("Division_ID"), rs.getString("Division"), rs.getInt("Country_ID"));
                divList.add(d);
            }
            divBox.setItems(divList);
        }
    }
    /**This method returns the user to the customer menu. */
    public void backToCustomers() throws IOException {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
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

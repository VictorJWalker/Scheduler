package Controllers;

import POJOs.Country;
import POJOs.Customer;
import POJOs.Division;
import helper.JDBC;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**This class is responsible for controlling the update customer screen. */
public class UpdateCustomerController { //this class is responsible for controlling the update customer screen

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

    private int currentUser;
    private String userName;
    private Customer targetCust;
    private ObservableList<Country> countryList;
    private ObservableList<Division> divList;
    /**This method populates the customer's info into the form and populates the combo box options.
      @param c The customer being updated. */
    public void setup(int id, String name, Customer c){         //Sets up the combo boxes and populates the input fields with the relevant information
        countryList = FXCollections.observableArrayList();
        divList = FXCollections.observableArrayList();
        currentUser = id;
        userName = name;
        targetCust = c;
        idBox.setText(Integer.toString(c.getCountryID()));

        try{
            String sql = "SELECT * FROM countries";
            PreparedStatement ps = JDBC.getConnection().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                Country newCountry = new Country(rs.getInt("Country_ID"), rs.getString("Country"));
                countryList.add(newCountry);
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
        countryBox.setItems(countryList);
        for(Country country:countryList){
            if(country.getCountryID() == targetCust.getCountryID()){
                countryBox.setValue(country);
                try{
                    updateDivisions();
                }catch(SQLException e){
                    e.printStackTrace();
                }
                break;
            }
        }
        for(Division div:divList){
            if(div.getDivisionID() == targetCust.getDivID()){
                divBox.setValue(div);
            }
        }
        nameBox.setText(targetCust.getName());
        idBox.setText(Integer.toString(targetCust.getCustomerID()));
        addressBox.setText(targetCust.getAddress());
        phoneBox.setText(targetCust.getPhone());
        zipBox.setText(targetCust.getZip());
    }
    /**Checks the input fields for validity, then updates the entry in the database and returns the user to the customers screen. */
    public void updateCustomer(){                      //tests inputs for validity, then updates entry in the database and returns to the customer window
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

                               //creates the customer to add
        targetCust.setName(nameBox.getText());
        targetCust.setAddress(addressBox.getText());
        targetCust.setZip(zipBox.getText());
        targetCust.setPhone(phoneBox.getText());
        targetCust.setDivID(divBox.getSelectionModel().getSelectedItem().getDivisionID());
        targetCust.setCountryID(divBox.getSelectionModel().getSelectedItem().getCountryID());
        try{
            targetCust.updateToDatabase();
        }catch(SQLException e){
            e.printStackTrace();
        }
        try {
            backToCustomers();
        }catch(IOException e){}
    }
    /**Changes the entries in the first level divisions combo box. Runs whenever the country box is changed. */

    public void updateDivisions() throws SQLException {     //updates the list of divisions
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
    /**Takes the user back to the customer menu. */
    public void backToCustomers() throws IOException {      //returns to the customers window
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

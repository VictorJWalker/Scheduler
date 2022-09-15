package Controllers;

import helper.JDBC;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.ResourceBundle;
import java.sql.*;
/**This class is responsible for controlling the Login screen. */
public class LoginController implements Initializable { //this class is responsible for controlling the Login screen
    @FXML
    private TextField userNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button submitButton;
    @FXML
    private Label passwordLabel;
    @FXML
    private Label userNameLabel;
    @FXML
    private Label titleLabel;
    @FXML
    private Label currentLoc;
    @FXML
    private Label currentLocLabel;
    @FXML
    private Label credPrompt;
    @FXML
    private Label errorMessage;
    private Locale locale;
    private ResourceBundle rb;
    private FileWriter fw;
    private PrintWriter pw;

    /**This method is run when the object is created. It detects the user's language and sets the text for labels. */
    public void initialize(URL url, ResourceBundle resourceBundle) { //Runs whenever the login form is opened. Detects language and sets label text.
        locale = Locale.getDefault();
        if(locale.getLanguage()!="fr"&& locale.getLanguage()!="en"){    //defaults to english if locale is outside en or fr
            locale = new Locale("en");
        }
            rb = ResourceBundle.getBundle("helper/login", locale);
            passwordLabel.setText(rb.getString("passwordLabel"));
            userNameLabel.setText(rb.getString("userNameLabel"));
            titleLabel.setText(rb.getString("titleLabel"));
            currentLocLabel.setText(rb.getString("currentLocLabel"));
            credPrompt.setText(rb.getString("credPrompt"));
            currentLoc.setText(ZoneId.systemDefault().toString());
            submitButton.setText(rb.getString("submitButton"));
        try {
            fw = new FileWriter("login_activity.txt", true);
            pw = new PrintWriter(fw);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
    /**This method is run when the user clicks the submit button.
     It checks the username and password field and logs the attempt in the login activity file. If the information matches the database, the user is taken to the main menu. Otherwise, an error message is displayed.*/
    public void submitButton() throws Exception{

        if(!userNameField.getText().isBlank()){
            pw.print("Login attempted by "+userNameField.getText()+" at "
                    + ZonedDateTime.of(LocalDateTime.now(),ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC",ZoneId.SHORT_IDS))
                    + ". Attempt was ");
        }
        else{
            pw.println("Login attempted by unidentified user at "
                       + ZonedDateTime.of(LocalDateTime.now(),ZoneId.systemDefault()).withZoneSameInstant(ZoneId.of("UTC",ZoneId.SHORT_IDS))
                       +". Attempt was unsuccessful.");
        }
        if(!userNameField.getText().isBlank() && !passwordField.getText().isBlank()){   //skips the connection and goes to the error message if either field is blank

            PreparedStatement ps = JDBC.getConnection().prepareStatement("SELECT * FROM users WHERE User_Name = ?");
            ps.setString(1,userNameField.getText());
            try(ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String pass = rs.getString("Password");
                    if (pass.equals(passwordField.getText())) {    //if it finds a matching username/password combination, opens the main menu
                        int id = rs.getInt("User_ID");
                        Stage stage = (Stage) passwordField.getScene().getWindow();
                        Stage newStage = new Stage();
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../FXML Files/MainMenu.fxml"));
                        Parent root = loader.load();
                        MainMenuController mainMenuController = loader.getController();
                        mainMenuController.setup(id, userNameField.getText());
                        newStage.setScene(new Scene(root));
                        stage.close();
                        newStage.show();
                        pw.print("successful.");
                        fw.close();
                        return;
                    }
                }
            }
            errorMessage.setText(rb.getString("wrongPass"));        //if the username and password don't match, displays an error message
            errorMessage.setVisible(true);
        }
        else{           //triggers if either field is left blank
            if(userNameField.getText().isBlank()){
                errorMessage.setText(rb.getString("noUser"));
            }
            else{
                errorMessage.setText(rb.getString("noPass"));
                pw.println("unsuccessful.");
            }
            errorMessage.setVisible(true);
        }

    }

}

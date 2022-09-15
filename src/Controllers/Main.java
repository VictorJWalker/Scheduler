package Controllers;
import helper.JDBC;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;
/**The main class. Opens the database connection and launches the application. */
public class Main extends Application {
    /**Launches the application. */
    @Override
    public void start(Stage primaryStage) throws Exception{         //bring up the first window
        Parent root = FXMLLoader.load(getClass().getResource("../FXML Files/Login.fxml"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**The main method. Opens the database connection and launches the application. */
    public static void main(String[] args) throws SQLException {
        JDBC.openConnection();          //open connection and launch application
        launch(args);
        JDBC.closeConnection();
    }
}

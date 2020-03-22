/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paddleexperience;

import DBAcess.ClubDBAccess;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.Member;

/**
 * FXML Controller class
 *
 * @author luukmaas
 */
public class FXMLController implements Initializable {
    
    @FXML
    private Button goToLoginButton, goToRegisterButton, loginButton, registerButton, loginCancelButton;
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void goToLogin(ActionEvent event) throws IOException {
        Stage stage = (Stage) goToLoginButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLLogin.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
        
    @FXML
    private void goHome(ActionEvent event) throws IOException {
        Stage stage;
        if (event.getSource() == loginCancelButton) {
            stage = (Stage) loginButton.getScene().getWindow();
        } else {
            stage = (Stage) registerButton.getScene().getWindow();
        }
        Parent root = FXMLLoader.load(getClass().getResource("FXMLWelcome.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void goToRegister(ActionEvent event) throws IOException {
        Stage stage = (Stage) goToRegisterButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLRegister.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void goToReservations(ActionEvent event) {
    }
    
    @FXML
    private void login(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        if (!(user.isEmpty() || pass.isEmpty())) {
            ClubDBAccess clubDBAccess = ClubDBAccess.getSingletonClubDBAccess();
            Member m = clubDBAccess.getMemberByCredentials(user, pass);
            if (!(m == null)) {
                System.out.println("Succesful login");
            } else {
                Alert a = new Alert(AlertType.ERROR);
                a.setTitle("Incorrect login");
                a.setHeaderText("Incorrect login '"+ user + "'");
                a.setContentText("The login and/or password were not found. Try again.");
                a.show();
            }
        } else {
            Alert a = new Alert(AlertType.INFORMATION);
            a.setTitle("Error");
            a.setContentText("Not all fields were entered.");
            a.show();
        }
    }
}

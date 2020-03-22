/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paddleexperience;

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
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author luukmaas
 */
public class FXMLController implements Initializable {
    
    @FXML
    private Button goToLoginButton, loginButton;
    
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
        Stage stage = (Stage) loginButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLWelcome.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void goToRegister(ActionEvent event) {
    }

    @FXML
    private void goToReservations(ActionEvent event) {
    }
    
    @FXML
    private void login(ActionEvent event) {
        
    }
}
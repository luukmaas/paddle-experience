/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paddleexperience;

import DBAcess.ClubDBAccess;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
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
    private TextField usernameField_login, nameField_register, surnameField_register, telephoneField_register, usernameField_register, passwordField_register, passwordConfirmationField_register, creditcardField_register, svcField_register;
    
    @FXML
    private PasswordField passwordField_login;
    
    @FXML
    private Label nameError, surnameError, telephoneError, usernameError, passwordError, passwordConfirmationError, creditcardNumberError, svcError, imageError;
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
        String user = usernameField_login.getText();
        String pass = passwordField_login.getText();
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
    
    @FXML
    private void register(ActionEvent event) {
        //Get data from input fields
        String name = nameField_register.getText();
        String surname = surnameField_register.getText();
        String telephone = telephoneField_register.getText();
        String login = usernameField_register.getText();
        String password = passwordField_register.getText();
        String passwordConfirmation = passwordConfirmationField_register.getText();
        String creditcard = creditcardField_register.getText();
        String svc = svcField_register.getText();
        //Image img = new Image("some url");
        
        //Validate data
        Member m = new Member(name, surname, telephone, login, password, creditcard, svc, null);
        RegisterValidator validator = new RegisterValidator(m, passwordConfirmation, svcField_register);
        ArrayList<String> errors = validator.validate();
        
        //If no errors, add data to database. Else, show errors in form
        if (validator.isValid()) {
            ClubDBAccess clubDBAccess = ClubDBAccess.getSingletonClubDBAccess();
            clubDBAccess.getMembers().add(m);
            System.out.println("Member " + name + " " + surname + " has been added succesfully.");   
        } else {
            nameError.setText(errors.get(0));
            surnameError.setText(errors.get(1));
            telephoneError.setText(errors.get(2));
            usernameError.setText(errors.get(3));
            passwordError.setText(errors.get(4));
            passwordConfirmationError.setText(errors.get(5));
            creditcardNumberError.setText(errors.get(6));
            svcError.setText(errors.get(7));
        }
        
        //Show success notification and return to home/login
    }
}

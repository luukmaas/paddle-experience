/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paddleexperience;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.TextField;
import model.Member;

/**
 *
 * @author luukmaas
 */
public class RegisterValidator {
    private String name;
    private String surname;
    private String telephone;
    private String login;
    private String password;
    private String creditcard;
    private String svc;
    private String passwordConfirmation;
    private TextField svcField;
    
    public RegisterValidator(Member m, String passConf, TextField svcInput) {
        this.name = m.getName();
        this.surname = m.getSurname();
        this.telephone = m.getTelephone();
        this.login = m.getLogin();
        this.password = m.getPassword();
        this.creditcard = m.getCreditCard();
        this.svc = m.getSvc();
        this.passwordConfirmation = passConf;
        this.svcField = svcInput;
    }
    
    public ArrayList<String> validate() {
        ArrayList<String> errors = new ArrayList<>();
        //Validate name is not empty or bigger than 50 chars.
        if (this.name.length() > 50) {
            errors.add("Name cannot be longer than 50 characters");
        } else if (this.name.isEmpty()) {
            errors.add("Name cannot be empty");
        } else {
            errors.add("");
        }
        
        //Validate surname is not empty or bigger than 50 chars.
        if (this.surname.length() > 50) {
            errors.add("Surname cannot be longer than 50 characters");
        } else if (this.surname.isEmpty()) {
            errors.add("Surname cannot be empty");
        } else {
            errors.add("");
        }
        
        //Validate telephone is not empty, only contains numbers and is not longer than 15 chars.
        if (this.telephone.isEmpty()) {
            errors.add("Telephone number cannot be empty");
        } else if (this.telephone.length() > 15 || this.telephone.length() < 8) {
            errors.add("Telephone number must be between 8 and 15 numbers."); 
        } else {
            try {
                Long.parseLong(this.telephone);
                errors.add("");
            } catch (Exception e) {
                errors.add("Telephone number can only contain numbers");
            }
        }
        
        //Validate username is not empty, only contains alphanumeric characters and is between 8-20 chars.
        Pattern p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);
        if (this.login.isEmpty()) {
            errors.add("Username cannot be empty");
        } else if (this.login.length() > 20 || this.login.length() < 8) {
            errors.add("Username must be between 8 and 20 characters");
        } else if (p.matcher(this.login).find()) {
            errors.add("Username can only contain letters and numbers");
        } else {
            errors.add("");
        }
        
        //Validate password is not empty, between 8-20 characters
        if (this.password.isEmpty()) {
            errors.add("Password cannot be empty.");
        } else if (this.password.length() < 8 || this.password.length() > 30) {
            errors.add("Password must be between 8 and 30 characters");
        } else {
            errors.add("");
        }
        
        //Validate password matches password confirmation (to avoid type-errors in password)
        if (!this.password.equals(this.passwordConfirmation)) {
            errors.add("The entered passwords do not match.");
        } else {
            errors.add("");
        }
        
        //Validate that if creditcard number has been entered, it must be 15 or 16 digits.
        if (!this.creditcard.isEmpty() && (this.creditcard.length() < 15 || this.creditcard.length() > 16)) {
            errors.add("Creditcard number must be 15 or 16 numbers");
        } else if (this.creditcard.isEmpty()) {
            errors.add("");
        } else {
            try {
                Long.parseLong(this.creditcard);
                errors.add("");
                
            } catch (Exception e) {
                errors.add("Creditcard number can only contain numbers");
            }
        }
        
        //Validate that if creditcard number has been entered, a 3-digit SVC has also been entered.
        if (!this.creditcard.isEmpty() && this.svc.isEmpty()) {
            errors.add("You need to enter your SVC code if you want to add your creditcard");
        } else if (!this.creditcard.isEmpty() && this.svc.length() != 3) {
            errors.add("Your SVC code must be 3 digits.");
        } else if (this.creditcard.isEmpty() && this.svc.isEmpty()) { 
            errors.add("");
        } else {
            try {
                Integer.parseInt(this.svc);
                errors.add("");
            } catch (Exception e) {
                errors.add("SVC code can only contain numbers");
            }
        }
        
        return errors;
    }
    
    public boolean isValid() {
        for (int i = 0; i<this.validate().size(); i++) {
            if (!"".equals(this.validate().get(i))) {
                return false;
            }
        }
        return true;
    }
}

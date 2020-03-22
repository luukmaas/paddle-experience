/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paddleexperience;

import java.util.ArrayList;
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
    
    public RegisterValidator(Member m) {
        this.name = m.getName();
        this.surname = m.getSurname();
        this.telephone = m.getTelephon();
        this.login = m.getLogin();
        this.password = m.getPassword();
        this.creditcard = m.getCreditCard();
        this.svc = m.getSvc();
    }
    
    public ArrayList<String> validate() {
        ArrayList<String> errors = new ArrayList<>();
        if (this.name.length() > 100) {
            errors.add(name);
        } 
        if (this.surname.length() > 100) {
            errors.add(surname);
        }
        try {
            Integer.parseInt(this.telephone);
        } catch (Exception e) {
            errors.add(this.telephone);
        }
        //...
        return errors;
    }
}

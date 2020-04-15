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
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Booking;
import model.Member;
//import org.fxmisc.easybind.EasyBind;

/**
 *
 * @author luukmaas
 */
public class FXMLControllerMyBookings implements Initializable {

    @FXML private TableView myBookingsTable;
    @FXML private Button logOutButton, myBookingsButton, backButton;
    
    private Member member;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
       
    public void setMember(Member m) {
        this.member = m;
    }
    
    @FXML
    private void goHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLWelcome.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
    }
    
    @FXML
    private void goToMain(ActionEvent event) throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));
        Parent root = (Parent) loader.load();
        FXMLControllerMain controller = loader.getController();
        System.out.println(member.getTelephone() + " (test)");
        controller.setMember(member);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
    }
    
    public void fillMyBookings(String login) {
        TableColumn dayColumn = (TableColumn) myBookingsTable.getColumns().get(0);
        TableColumn courtCol = (TableColumn) myBookingsTable.getColumns().get(1);
        TableColumn timeCol = (TableColumn) myBookingsTable.getColumns().get(2);
        TableColumn paidColumn = (TableColumn) myBookingsTable.getColumns().get(3);
        
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("madeForDay"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("fromTime"));
        
        courtCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Booking, String> p) {
                return new SimpleStringProperty(p.getValue().getCourt().getName());
            }
        });
        paidColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Booking, String> p) {
                if (p.getValue().getPaid() == true) {
                    return new SimpleStringProperty("Paid");
                } else {
                    return new SimpleStringProperty("Not yet paid");
                }
            }
        });
        myBookingsTable.setItems(this.userBookings(login));
    }
    
    public ObservableList<Booking> userBookings(String login) {
        ArrayList<Booking> bookings = ClubDBAccess.getSingletonClubDBAccess().getUserBookings(login); //All bookings for user
        
        ObservableList observableSlots = FXCollections.observableList(bookings);
        return observableSlots;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paddleexperience;

import DBAcess.ClubDBAccess;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Booking;
import model.Court;
import model.Member;

/**
 *
 * @author luukmaas
 */
public class FXMLControllerMain implements Initializable {

    @FXML
    private TableView bookCourtTable;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button logOutButton, bookButton;
    @FXML
    private Label welcomeLabel;
    
    private Member member;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());
    }
       
    public void setMember(Member m) {
        this.member = m;
    }
    
    @FXML
    private void goHome(ActionEvent event) throws IOException {
        Stage stage;
        stage = (Stage) logOutButton.getScene().getWindow();
        double height = logOutButton.getScene().getWindow().getHeight();
        double width = logOutButton.getScene().getWindow().getWidth();
        stage.setHeight(height);
        stage.setWidth(width);

        Parent root = FXMLLoader.load(getClass().getResource("FXMLWelcome.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    private void showBookCourt() {
        
        this.fillTable(datePicker.getValue());
        
        bookCourtTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null ){
                bookButton.setDisable(false);                
            }
        });
    }

    private void fillTable(LocalDate date) {
        TableColumn timeColumn = (TableColumn) bookCourtTable.getColumns().get(0);
        TableColumn courtColumn = (TableColumn) bookCourtTable.getColumns().get(1);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("fromTime"));
        courtColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Booking, String> p) {
                return new SimpleStringProperty(p.getValue().getCourt().getName());
            }
        });
        
        bookCourtTable.setItems(this.bookCourtTableData(date));
        bookCourtTable.getSortOrder().add(timeColumn);
    }
 
    public ObservableList<Booking> emptySlots(LocalDate date) {
        ArrayList<Court> courts = ClubDBAccess.getSingletonClubDBAccess().getCourts(); //All courts
        ArrayList<Booking> bookings = ClubDBAccess.getSingletonClubDBAccess().getForDayBookings(date); //All bookings for date
        ArrayList<Booking> slots = new ArrayList<>();
        
        for (Court c : courts) {
            LocalTime time = LocalTime.of(9, 0);
            while (time.isBefore(LocalTime.of(21, 1))) {
                slots.add(new Booking(null, date, time, false, c, null));
                time = time.plusHours(1);
                time = time.plusMinutes(30);
            }   
            
        }
        ObservableList observableSlots = FXCollections.observableList(slots);
        return observableSlots;
    }
    
    public ObservableList<Booking> bookCourtTableData(LocalDate date) {
        ObservableList<Booking> slots = this.emptySlots(date); //All slots for date
        ArrayList<Booking> bookings = ClubDBAccess.getSingletonClubDBAccess().getForDayBookings(date); //Occupied slots      
        
        bookings.stream().forEach((b) -> {
            Iterator it = slots.iterator();
            System.out.println(b.getCourt().getName() + " " + b.getFromTime());
            while (it.hasNext()){
                Booking b2 = (Booking) it.next();
                if (b.getFromTime().equals(b2.getFromTime()) && (b.getCourt().getName().equals(b2.getCourt().getName()))) { //Remove free slots that coincide with an occupied slot
                    it.remove();
                    System.out.println("Deleted " + b2.getCourt().getName() + " " + b2.getFromTime());
                }
            }
        });
        return slots;
    }
    
    @FXML
    public void book() {        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));
            Parent root = loader.load();
            FXMLController c = loader.<FXMLController>getController();
            System.out.println(c.getMember().getLogin());
        } catch (Exception e) {
        
        }
        
        Booking b = (Booking) bookCourtTable.getSelectionModel().getSelectedItem();
        
        LocalDateTime today = LocalDateTime.now();
        LocalDate date = datePicker.getValue();
        LocalTime fromHour = b.getFromTime();
        Boolean hasPaid = this.member.checkHasCreditInfo();
        Court court = b.getCourt();        
        Member m = this.member;
        
        Booking booking = new Booking(today, date, fromHour, hasPaid, court, m);
        
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Booking confirmation");
        a.setHeaderText("Confirm your booking.");
        a.setContentText("Are you sure you want to book " + court.getName() + " at date " + date.toString() + " at time " + fromHour.toString() + "?");
       
        Optional<ButtonType> result = a.showAndWait();
        if(!result.isPresent() || result.get() == ButtonType.CANCEL) {
            System.out.println("Booking cancelled.");
        } else if(result.get() == ButtonType.OK) {
            ClubDBAccess.getSingletonClubDBAccess().getBookings().add(booking);
            ClubDBAccess.getSingletonClubDBAccess().saveDB(); 
            this.showBookCourt();
        }
        
    }
}

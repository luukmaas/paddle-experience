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

    @FXML private TableView bookCourtTable;
    @FXML private DatePicker datePicker;
    @FXML private Button logOutButton, bookButton, myBookingsButton, goBackButton, infoButton;
    
    private Member member;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        datePicker.setValue(LocalDate.now());   
        this.fillTable(LocalDate.now());
        datePicker.valueProperty().addListener((observable, oldDate, newDate)-> {
            this.fillTable(newDate);
        });
        bookCourtTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null ){
                bookButton.setDisable(false);                
            }
        });
    }
       
    public void setMember(Member m) {
        this.member = m;
    }
    
    @FXML
    private void goToHome(ActionEvent event) throws IOException {
        Stage stage;
        if (event.getSource() == goBackButton) {
            stage = (Stage) goBackButton.getScene().getWindow();
        } else {
            stage = (Stage) logOutButton.getScene().getWindow();
        } 
        
        Parent root = FXMLLoader.load(getClass().getResource("FXMLWelcome.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
    }
    
    @FXML
    private void goToInfo(ActionEvent event) throws IOException {
        Stage stage = (Stage) infoButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLInfo.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
    }
    
    @FXML
    private void showMyBookings(ActionEvent event) throws IOException {
        Stage stage = (Stage) myBookingsButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMyBookings.fxml"));
        Parent root = (Parent) loader.load();
        FXMLControllerMyBookings controller = loader.getController();
        controller.setMember(member);
        controller.fillMyBookings(this.member.getLogin());
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);  
    }
       
    private void fillTable(LocalDate date) {
        TableColumn timeColumn = (TableColumn) bookCourtTable.getColumns().get(0);
        TableColumn courtColumn = (TableColumn) bookCourtTable.getColumns().get(1);
        TableColumn availabilityColumn = (TableColumn) bookCourtTable.getColumns().get(2);
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("fromTime"));
        courtColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Booking, String> p) {
                return new SimpleStringProperty(p.getValue().getCourt().getName());
            }
        });
        availabilityColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Booking, String> p) {
                if (p.getValue().getMember() == null) {
                    return new SimpleStringProperty("Free");
                } else {
                    return new SimpleStringProperty("Booked by "+ p.getValue().getMember().getLogin());
                }
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
            while (it.hasNext()){
                Booking b2 = (Booking) it.next();
                if (b2.getMember() == null && b.getFromTime().equals(b2.getFromTime()) && (b.getCourt().getName().equals(b2.getCourt().getName()))) { //Remove free slots that coincide with an occupied slot
                    it.remove();
                }
            }
        });
        slots.addAll(bookings);
        return slots;
    }
    
    @FXML
    public void book() {        
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
            //Booking is cancelled, nothing happens
        } else if(result.get() == ButtonType.OK) {
            ClubDBAccess.getSingletonClubDBAccess().getBookings().add(booking);
            ClubDBAccess.getSingletonClubDBAccess().saveDB(); 
            this.fillTable(datePicker.getValue());
        }
    }
}

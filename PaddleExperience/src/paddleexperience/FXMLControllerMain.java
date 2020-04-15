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
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Booking;
import model.Court;
import model.Member;
import org.fxmisc.easybind.EasyBind;

/**
 *
 * @author luukmaas
 */
public class FXMLControllerMain implements Initializable {

    @FXML
    private TableView bookCourtTable;
    private TableView myBookingsTable;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button logOutButton, bookButton, myBookingsButton, backButton;
    
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
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLWelcome.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        
    }
    
    @FXML
    private void goToMain(ActionEvent event) throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLMain.fxml"));
        Scene scene = new Scene(root);
        //scene.getStylesheets().addAll(this.getClass().getResource("style.css").toExternalForm());
        //scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
       // scene.getStylesheets().add("path/style.css");
    }
    
    @FXML
    private void showMyBookings(ActionEvent event) throws IOException {
        Stage stage = (Stage) myBookingsButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLMybookings.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
                
        //how do I get the member?
        //this.fillMyBookings(root.getMember());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));
        FXMLController c = loader.<FXMLController>getController();
        
        // c.getMember().getLogin() is a string
        this.fillMyBookings(c.getMember().getLogin());
    }
    
    private void fillMyBookings(String login) {
        
        TableColumn dayColumn = (TableColumn) myBookingsTable.getColumns().get(0);
        TableColumn courtCol = (TableColumn) myBookingsTable.getColumns().get(1);
        TableColumn timeCol = (TableColumn) myBookingsTable.getColumns().get(2);
        TableColumn paidColumn = (TableColumn) myBookingsTable.getColumns().get(3);
        TableColumn delColumn = (TableColumn) myBookingsTable.getColumns().get(4);

        
        /*timeCol.setCellValueFactory(new PropertyValueFactory<>("fromTime"));
        courtCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Booking, String> p) {
                return new SimpleStringProperty(p.getValue().getCourt().getName());
            }
        });*/
        
       // myBookingsTable.setItems(this.userBookings(login));
        //myBookingsTable.getSortOrder().add(timeCol);
        
        ClubDBAccess clubDBAcess;
        clubDBAcess = ClubDBAccess.getSingletonClubDBAccess();
        ObservableList<Booking> observableBookings;
        observableBookings = FXCollections.observableList(clubDBAcess.getUserBookings(login));
        myBookingsTable.setItems(observableBookings);
    }
    
    public ObservableList<Booking> userBookings(String login) {
        ArrayList<Booking> bookings = ClubDBAccess.getSingletonClubDBAccess().getUserBookings(login); //All bookings for user
        ArrayList<Booking> slots = new ArrayList<>();
        
        ObservableList observableSlots = FXCollections.observableList(slots);
        return observableSlots;
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
                    return new SimpleStringProperty(p.getValue().getMember().getLogin());
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
            System.out.println(b.getCourt().getName() + " " + b.getFromTime());
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

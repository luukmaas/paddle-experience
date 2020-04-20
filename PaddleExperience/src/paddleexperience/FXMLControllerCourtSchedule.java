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
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Booking;
import model.Court;


/**
 * FXML Controller class
 *
 * @author vilmaahlholm
 */
public class FXMLControllerCourtSchedule implements Initializable {
    
    @FXML private TableView bookCourtTable;
    @FXML private Button goBackButton;
    @FXML private Label dateLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.fillTable(LocalDate.now());
        this.dateLabel.setText("All bookings of today, " + LocalDate.now().getDayOfMonth() + "/" + LocalDate.now().getMonthValue() + ".");
    }    
    
    
    @FXML
    private void goToHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) goBackButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLWelcome.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);  
    }
       
    private void fillTable(LocalDate date) {
        TableColumn timeColumn = (TableColumn) bookCourtTable.getColumns().get(0);
        TableColumn courtColumn = (TableColumn) bookCourtTable.getColumns().get(1);
        TableColumn availabilityColumn = (TableColumn) bookCourtTable.getColumns().get(2);
        timeColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Booking, String> p) {
                String from = p.getValue().getFromTime().toString();
                String to = p.getValue().getFromTime().plusHours(1).plusMinutes(30).toString();
                return new SimpleStringProperty(from + " - " + to);
            }
        });
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
    
    
}

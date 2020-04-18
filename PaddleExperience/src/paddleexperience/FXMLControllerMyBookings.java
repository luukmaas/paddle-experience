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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Booking;
import model.Member;

/**
 *
 * @author luukmaas
 */
public class FXMLControllerMyBookings implements Initializable {

    @FXML private TableView myBookingsTable;
    @FXML private Button logOutButton, backButton;
    @FXML private ImageView userImg;
    @FXML private Label greetLabel1, greetLabel2;


    
    private Member member;
    private static final int MAX_ITEMS = 10;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
       
    public void setMember(Member m) {
        this.member = m;
    }
    
    @FXML
    private void goToHome(ActionEvent event) throws IOException {
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("FXMLWelcome.fxml"));
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);  
    }
    
    @FXML
    private void goToMain(ActionEvent event) throws IOException {
        Stage stage = (Stage) backButton.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMain.fxml"));
        Parent root = (Parent) loader.load();
        FXMLControllerMain controller = loader.getController();
        controller.setMember(member);
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
    }
    
    public void fillMyBookings(String login) {
        TableColumn dayColumn = (TableColumn) myBookingsTable.getColumns().get(0);
        TableColumn courtCol = (TableColumn) myBookingsTable.getColumns().get(1);
        TableColumn timeCol = (TableColumn) myBookingsTable.getColumns().get(2);
        TableColumn paidColumn = (TableColumn) myBookingsTable.getColumns().get(3);
        TableColumn deleteBtnColumn = (TableColumn) myBookingsTable.getColumns().get(4);
        
        deleteBtnColumn.setCellValueFactory(new PropertyValueFactory<>("DUMMY"));
        Callback<TableColumn<Booking, String>, TableCell<Booking, String>> cellFactory = 
        (final TableColumn<Booking, String> param) -> {
            final TableCell<Booking, String> cell = new TableCell<Booking, String>() {
                
                Button btn = new Button("");
                public boolean isNotWithin24hours(Booking b) {
                    LocalDate date = b.getMadeForDay();
                    LocalTime time = b.getFromTime();
                    if (date.isAfter(LocalDate.now().plusDays(1))) {
                        return true;
                    } else {
                        if (date.isEqual(LocalDate.now())) {
                            return false;
                        } else return LocalDateTime.now().until(LocalDateTime.of(date, time), ChronoUnit.HOURS) >= 24;
                    }
                }
                
                @Override
                public void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        Booking booking = getTableView().getItems().get(getIndex());
                        btn.setOnAction(event -> {
                            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                            a.setTitle("Deleting booking");
                            a.setHeaderText("Deleting your booking.");
                            a.setContentText("Are you sure you want to delete your booking of " + booking.getCourt().getName() + " at date " + booking.getMadeForDay().toString() + " at time " + booking.getFromTime().toString() + "?");

                            Optional<ButtonType> result = a.showAndWait();
                            if(!result.isPresent() || result.get() == ButtonType.CANCEL) {
                                //Deleting is cancelled, nothing happens
                            } else if(result.get() == ButtonType.OK) {
                                ClubDBAccess.getSingletonClubDBAccess().getBookings().remove(booking);
                                ClubDBAccess.getSingletonClubDBAccess().saveDB();

                                try {    
                                    Stage stage = (Stage) myBookingsTable.getScene().getWindow();
                                    FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMyBookings.fxml"));
                                    Parent root = (Parent) loader.load();
                                    FXMLControllerMyBookings controller = loader.getController();
                                    controller.setMember(member);
                                    controller.fillMyBookings(member.getLogin());
                                    Scene scene = new Scene(root);
                                    scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());  
                                    stage.setScene(scene);
                                } catch (IOException ex) {
                                    Logger.getLogger(FXMLControllerMyBookings.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }

                        });
                        setGraphic(btn);
                        setText(null);
                    }
                }
            };
            return cell;
        };
        deleteBtnColumn.setCellFactory(cellFactory);
        dayColumn.setCellValueFactory(new PropertyValueFactory<>("madeForDay"));
        timeCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Booking, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Booking, String> p) {
                String from = p.getValue().getFromTime().toString();
                String to = p.getValue().getFromTime().plusHours(1).plusMinutes(30).toString();
                return new SimpleStringProperty(from + " - " + to);
            }
        });
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
        
        ObservableList<Booking> data = this.userBookings(login);

        SortedList<Booking> sortedList = new SortedList<>(data, 
            (Booking booking1, Booking booking2) -> {
                if( booking1.getBookingDate().isBefore(booking2.getBookingDate()) ) {
                    return -1;
                } else if( booking1.getBookingDate().isAfter(booking2.getBookingDate()) ) {
                    return 1;
                } else {
                    return 0;
                }
        });
        
        FilteredList<Booking> filteredData = new FilteredList<>(
            sortedList,
            booking -> data.indexOf(booking) < MAX_ITEMS
        );

        myBookingsTable.setItems(filteredData);
    }
    
    public ObservableList<Booking> userBookings(String login) {
        ArrayList<Booking> bookings = ClubDBAccess.getSingletonClubDBAccess().getUserBookings(login); //All bookings for user
        
        //Filter away all bookings from the past
        Iterator it = bookings.iterator();
        while (it.hasNext()) {
            Booking b = (Booking) it.next();
            if (b.getMadeForDay().isBefore(LocalDate.now())) {
                it.remove();
            }
        }
        
        ObservableList observableSlots = FXCollections.observableList(bookings);
        return observableSlots;
    }
    
    
    public void displayUserImage(Member memberImg) {
        userImg.setImage(memberImg.getImage());
        userImg.setFitHeight(100);
        userImg.setFitWidth(100);

    }
    
    
    public void greetUser(Member memberInput) {
        Label greet1;
        greet1 = (Label) greetLabel1;
        //this.setMember(member);
        greet1.setText("Hi " + memberInput.getName());
        
        Label greet2;
        greet2 = (Label) greetLabel2;
        greet2.setText("here are your latest bookings!");
        //greet.setText(member.getLogin());

    }

}

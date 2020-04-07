/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paddleexperience;

import java.time.LocalDate;
import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author luukmaas
 */
public class BookingSlot {
    private final LocalDate date;
    private final ObjectProperty<LocalDateTime> time;
    private final StringProperty courtName;
    private BooleanProperty occupied;
    
    public BookingSlot(LocalDate date_, LocalDateTime time_, String courtName_, boolean occupied_) {
        this.date = date_;
        this.time = new SimpleObjectProperty<>(time_);
        this.courtName = new SimpleStringProperty(courtName_);
        this.occupied = new SimpleBooleanProperty(occupied_);
    }
    
    public ObjectProperty<LocalDateTime> timeProperty() {
        return this.time;
    }
    
    public StringProperty courtNameProperty() {
        return this.courtName;
    }
    
    public BooleanProperty occupiedProperty() {
        return this.occupied;
    }
    
    public LocalDate getDate() {
        return this.date;
    }
}

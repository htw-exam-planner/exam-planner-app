package admin.controllers;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import models.Group;

import java.io.IOException;

public class GroupEntry extends HBox {
    public static final EventType<Event> GROUP_DELETED =
            new EventType<>("GROUP_DELETED");
    private Group group;

    @FXML
    Label groupLabel;

    @FXML
    Label statusLabel;

    /**
     * Creates a new Group entry for the specified Group
     * @param group The entry's Group
     */
    public GroupEntry(Group group) {
        this.group = group;

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/admin/views/GroupEntry.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Displays the Groups name and booking status
     */
    public void paint(){
        try {
            groupLabel.setText(group.toString());

            boolean hasReservation = group.hasReservation();
            boolean hasBooking = group.hasBooking();

            String statusText = hasBooking ? "Gebucht" : hasReservation ? "Reserviert" : "Nicht gebucht";
            Color color = hasBooking ? Color.MEDIUMSEAGREEN : hasReservation ? Color.DARKGOLDENROD : Color.DARKORANGE;

            statusLabel.setText(statusText);
            statusLabel.setTextFill(color);
        } catch (Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Anzeigen der Gruppen");
            alert.showAndWait();
            System.exit(1);
        }
    }

    /**
     * Deletes the Group and fires the event to reload the View
     */
    public void delete(){
        try {
            Group.delete(group);
            fireEvent(new Event(GROUP_DELETED));
        } catch (Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Löschen der Gruppe");
            alert.showAndWait();
            System.exit(1);

        }
    }
}

package admin.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import models.Group;

import java.io.IOException;

public class AdminGroups extends AnchorPane {
    @FXML
    VBox entries;

    public AdminGroups(){
        initializeView();
    }

    /**
     * Loads the JavaFX view component
     *
     * This can not be done in the constructor, because initialization logic of extended classes might not be done otherwise
     */
    protected void initializeView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/admin/views/AdminGroups.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    /**
     * Puts the Groups into the Group view
     */
    @FXML
    public void initialize(){
        showGroups();
    }

    private void showGroups(){
        try {
            entries.getChildren().clear();

            for (Group g: Group.all()) {
                GroupEntry entry = new GroupEntry(g);

                entry.addEventHandler(GroupEntry.GROUP_DELETED, event -> showGroups());

                entry.paint();

                entries.getChildren().add(entry);
            }
        } catch (Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Anzeigen der Gruppen");
            alert.showAndWait();
            System.exit(1);

        }
    }

    /**
     * Adds a new Group and reloads the view
     * @param event The click event
     */
    public void add(ActionEvent event){
        try {
            Group.create();
            showGroups();
        } catch (Exception e){
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Fehler beim Erstellen der Gruppe");
            alert.showAndWait();
            System.exit(1);
        }
    }
}

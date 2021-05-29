package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.fxml.FXML;
import model.Bug;
import model.BugPriority;
import model.BugStatus;
import services.IService;
import services.ServiceException;


public class AddBugController {

    @FXML
    private TextField textName;
    @FXML
    private TextField textDescription;
    @FXML
    ComboBox<BugPriority> priorityBox;

    private IService server;

    public void setServer(IService s){
        server = s;
    }

    public void initialize(){
        priorityBox.setItems(FXCollections.observableArrayList(
                BugPriority.LOW,
                BugPriority.MEDIUM,
                BugPriority.HIGH));
    }

    @FXML
    public void handleAddBug(ActionEvent actionEvent) {
        String name = textName.getText();
        String desc = textDescription.getText();
        BugPriority priority = priorityBox.getValue();

        Bug bug = new Bug(name, desc, priority, BugStatus.ACTIVE);
        try{
            Bug added = server.registerBug(bug);
            if(added == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Add bug error");
                alert.setHeaderText("DB error");
                alert.setContentText("Error adding bug");
                alert.show();
            }
        } catch (ServiceException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Add bug error");
            alert.setHeaderText("Service error");
            alert.setContentText(e.getErrors());
            alert.show();
        }
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

}
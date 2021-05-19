package gui;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;

import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.fxml.FXML;
import model.Bug;
import model.BugPriority;
import model.Verifier;
import services.IService;
import services.ServiceException;


public class AddBugController {

    @FXML
    private TextField textName;
    @FXML
    private TextField textDescription;
    @FXML
    ComboBox<String> priorityBox;

    private IService server;

    public void setServer(IService s){
        server = s;
    }

    public void initialize(){
        priorityBox.setItems(FXCollections.observableArrayList(
                BugPriority.LOW.toString(),
                BugPriority.MEDIUM.toString(),
                BugPriority.HIGH.toString()));
    }

    @FXML
    public void handleAddBug(ActionEvent actionEvent) {
        String name = textName.getText();
        String desc = textDescription.getText();
        String priorityString = priorityBox.getValue();
        BugPriority priority = null;
        if(priorityString.equals("LOW")){
            priority = BugPriority.LOW;
        }
        if(priorityString.equals("MEDIUM")){
            priority = BugPriority.MEDIUM;
        }
        if(priorityString.equals("HIGH")){
            priority = BugPriority.HIGH;
        }
        Bug bug = new Bug(name, desc, priority);
        try{
            server.registerBug(bug);
        } catch (ServiceException e){
            e.printStackTrace();
        }
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

}
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


public class UpdateBugController{
    @FXML
    private TextField textName;
    @FXML
    private TextField textDescription;
    @FXML
    ComboBox<BugPriority> priorityBox;

    private IService server;
    private Long bugId;

    public void setServer(IService s){
        server = s;
    }

    public void setParams(Long id, String name, String desc, BugPriority priority){
        bugId = id;
        textName.setText(name);
        textDescription.setText(desc);
        priorityBox.getSelectionModel().select(priority);
    }

    public void initialize(){
        priorityBox.setItems(FXCollections.observableArrayList(
                BugPriority.LOW,
                BugPriority.MEDIUM,
                BugPriority.HIGH));
    }

    @FXML
    public void handleUpdateBug(ActionEvent actionEvent) {
        String name = textName.getText();
        String desc = textDescription.getText();
        BugPriority priority = priorityBox.getValue();
        Bug bug = new Bug(bugId, name, desc, priority);
        try{
            server.updateBug(bug);
        } catch (ServiceException e){
            e.printStackTrace();
        }
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

}
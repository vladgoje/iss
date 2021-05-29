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


public class UpdateBugController{
    @FXML
    private TextField textName;
    @FXML
    private TextField textDescription;
    @FXML
    ComboBox<BugPriority> priorityBox;
    @FXML
    ComboBox<BugStatus> statusBox;

    private IService server;
    private Long bugId;
    private BugStatus bugStatus;

    public void setServer(IService s){
        server = s;
    }

    public void setParams(Long id, String name, String desc, BugPriority priority, BugStatus status){
        bugId = id;
        textName.setText(name);
        textDescription.setText(desc);
        priorityBox.getSelectionModel().select(priority);
        statusBox.getSelectionModel().select(status);
    }

    public void initialize(){
        priorityBox.setItems(FXCollections.observableArrayList(
                BugPriority.LOW,
                BugPriority.MEDIUM,
                BugPriority.HIGH));

        statusBox.setItems(FXCollections.observableArrayList(
                BugStatus.ACTIVE,
                BugStatus.PENDING,
                BugStatus.SOLVED));
    }

    @FXML
    public void handleUpdateBug(ActionEvent actionEvent) {
        String name = textName.getText();
        String desc = textDescription.getText();
        BugPriority priority = priorityBox.getValue();
        BugStatus status = statusBox.getValue();

        Bug bug = new Bug(bugId, name, desc, priority, status);
        try{
            Bug modified = server.updateBug(bug);
            if(modified == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Update bug error");
                alert.setHeaderText("DB error");
                alert.setContentText("Error updating bug");
                alert.show();
            }
        } catch (ServiceException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Update bug error");
            alert.setHeaderText("Service error");
            alert.setContentText(e.getErrors());
            alert.show();
        }
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

}
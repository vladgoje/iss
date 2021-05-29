package gui;

import javafx.event.ActionEvent;

import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.fxml.FXML;
import model.Employee;
import model.Programmer;
import model.Verifier;
import services.IService;
import services.ServiceException;


public class RegisterController{

    @FXML
    private Label registerNotification;
    @FXML
    private TextField textUsername;
    @FXML
    private TextField textPassword;

    private IService server;

    public void setServer(IService s){
        server = s;
    }

    public void initialize(){
    }

    @FXML
    public void handleRegisterProgrammer(ActionEvent actionEvent) {
        String username = textUsername.getText();
        String password = textPassword.getText();
        Programmer programmer = new Programmer(username, password);
        try{
            Programmer added = server.register(programmer);
            if(added == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Add bug error");
                alert.setHeaderText("DB error");
                alert.setContentText("Error adding bug");
                alert.show();
            } else {
                ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            }
        } catch (ServiceException e){
            registerNotification.setText(e.getErrors());
        }

    }

    @FXML
    public void handleRegisterVerifier(ActionEvent actionEvent) {
        String username = textUsername.getText();
        String password = textPassword.getText();
        Verifier verifier = new Verifier(username, password);
        try{
            Verifier added = server.register(verifier);
            if(added == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Add bug error");
                alert.setHeaderText("DB error");
                alert.setContentText("Error adding bug");
                alert.show();
            } else {
                ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            }
        } catch (ServiceException e) {
            registerNotification.setText(e.getErrors());
        }
    }

}
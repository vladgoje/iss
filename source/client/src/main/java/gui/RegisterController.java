package gui;

import javafx.event.ActionEvent;

import javafx.scene.Node;
import javafx.scene.control.*;

import javafx.fxml.FXML;
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
    public void handleRegister(ActionEvent actionEvent) {
        String username = textUsername.getText();
        String password = textPassword.getText();
        Verifier verifier = new Verifier(username, password);
        try{
            server.register(verifier);
        } catch (ServiceException e){
            registerNotification.setText(e.getErrors());
        }
        ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
    }

}
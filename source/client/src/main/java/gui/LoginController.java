package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import model.Programmer;
import model.Verifier;
import services.IService;
import services.ServiceException;

import java.io.IOException;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class LoginController extends UnicastRemoteObject implements Serializable {

    private IService server;
    MainController mainController;
    Parent mainParent;
    @FXML
    public Label loginNotification;
    @FXML
    public TextField textUsername;
    @FXML
    public TextField textPassword;
    @FXML
    public Button registerButton;

    public LoginController() throws RemoteException {
    }

    public void setServer(IService s){
        server=s;
    }

    public void setMainController(MainController mainController) { this.mainController = mainController; }

    public void setMainParent(Parent mainParent) { this.mainParent = mainParent; }

    public void initialize(){
    }

    @FXML
    public void handleVerifierLogin(ActionEvent actionEvent) {
        String username = textUsername.getText();
        String password = textPassword.getText();
        Verifier verifier = new Verifier(username, password);
        try{
            server.loginVerifier(verifier, mainController);
            Stage stage=new Stage();
            stage.setTitle("Main Window for " + verifier.getUsername());
            stage.setScene(new Scene(mainParent));

            stage.setOnCloseRequest(event -> {
                mainController.logoutVerifier();
                System.exit(0);
            });

            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.show();
            mainController.setConnectedVerifier(verifier);
            mainController.initialize();

            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch(ServiceException e) {
            loginNotification.setText(e.getErrors());
        }
    }

    public void handleProgrammerLogin(ActionEvent actionEvent) {
        String username = textUsername.getText();
        String password = textPassword.getText();
        Programmer programmer = new Programmer(username, password);
        try{
            server.loginProgrammer(programmer, mainController);
            Stage stage=new Stage();
            stage.setTitle("Main Window for " + programmer.getUsername());
            stage.setScene(new Scene(mainParent));

            stage.setOnCloseRequest(event -> {
                mainController.logoutProgrammer();
                System.exit(0);
            });

            stage.setMaximized(false);
            stage.setMaximized(true);
            stage.show();
            mainController.setConnectedProgrammer(programmer);
            mainController.initModel();

            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

        } catch(ServiceException e) {
            loginNotification.setText(e.getErrors());
        }
    }


    @FXML
    public void handleRegister(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/RegisterView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Inregistrare");
            stage.setScene(new Scene(root));
            RegisterController registerController = loader.getController();
            registerController.setServer(server);
            stage.show();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

}
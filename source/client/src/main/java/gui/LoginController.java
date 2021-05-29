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
import model.Employee;
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
    Parent mainParent;
    @FXML
    public Label loginNotification;
    @FXML
    public TextField textUsername;
    @FXML
    public TextField textPassword;
    @FXML
    public Button registerButton;

    public LoginController() throws RemoteException { }

    public void setServer(IService s){
        server=s;
    }

    public void initialize(){
    }

    @FXML
    public void handleLogin(ActionEvent actionEvent) {
        String username = textUsername.getText();
        String password = textPassword.getText();
        Employee employee = new Employee(username, password);
        try{
            try{
                Employee loggedIn = server.login(employee);
                FXMLLoader mainLoader = new FXMLLoader();

                Stage stage=new Stage();
                stage.setTitle("Bug Tracking System " + employee.getUsername());


                if(loggedIn instanceof Programmer){
                    mainLoader.setLocation(getClass().getResource("/views/ProgrammerMainView.fxml"));
                    this.mainParent = mainLoader.load();
                    MainProgrammerController mainController = mainLoader.getController();
                    mainController.setServer(server);
                    mainController.setConnectedProgrammer((Programmer) loggedIn);
                    mainController.initialize();
                    stage.setScene(new Scene(mainParent));
                    stage.setOnCloseRequest(event -> {
                        mainController.logout();
                        System.exit(0);
                    });
                } else {
                    mainLoader.setLocation(getClass().getResource("/views/VerifierMainView.fxml"));
                    this.mainParent = mainLoader.load();
                    MainVerifierController mainController = mainLoader.getController();
                    mainController.setServer(server);
                    mainController.setConnectedVerifier((Verifier) loggedIn);
                    mainController.initialize();
                    stage.setScene(new Scene(mainParent));
                    stage.setOnCloseRequest(event -> {
                        mainController.logout();
                        System.exit(0);
                    });
                }

                stage.show();
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();

            } catch (IOException e){
                e.printStackTrace();
            }
        } catch(ServiceException e) {
            loginNotification.setText(e.getMessage());
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
package gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.Bug;
import model.BugPriority;
import model.Programmer;
import model.Verifier;
import services.IObserver;
import services.IService;
import services.ServiceException;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ResourceBundle;

public class MainController extends UnicastRemoteObject implements Serializable, Initializable, IObserver {
    /************FRIEND CELL****************/
    static class BugCell extends ListCell<Bug> {
        HBox hbox = new HBox();
        Label name = new Label("(empty)");
        Label description = new Label("(empty)");
        Label priority = new Label("(empty)");
        Pane pane1 = new Pane();
        Pane pane2= new Pane();
        Bug lastItem;

        public BugCell() {
            super();

            pane1.setMinWidth(2);
            pane2.setMinWidth(2);

            this.setOnMouseClicked((mouseEvent) -> {

            });

            this.setStyle("-fx-cursor: hand");

            hbox.getChildren().addAll(name, pane1, description, pane2, priority);
            hbox.setAlignment(Pos.CENTER);
            HBox.setHgrow(pane1, Priority.ALWAYS);
            HBox.setHgrow(pane2, Priority.ALWAYS);
        }

        @Override
        protected void updateItem(Bug item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                if(item != null){
                    name.setText(item.getName());
                    description.setText(item.getDescription());
                    priority.setText(item.getPriority().toString());
                }
                setGraphic(hbox);
            }
        }
    }

    private IService server;

    Verifier verifier;
    Programmer programmer;

    ObservableList<Bug> bugsModel = FXCollections.observableArrayList();
//    ObservableList<BugRequest> requestModel = FXCollections.observableArrayList();

    @FXML
    Label connectedLabel;
    @FXML
    ListView<Bug> bugListView;
    @FXML
    ListView<Bug> requestListView;
    public MainController() throws RemoteException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setServer(IService s){
        server=s;
    }

    public void setConnectedVerifier(Verifier verifier){
        this.verifier = verifier;
        connectedLabel.setText("Connected verifier: " + verifier.getUsername());
    }

    public void setConnectedProgrammer(Programmer programmer){
        this.programmer = programmer;
        connectedLabel.setText("Connected programmer: " + programmer.getUsername());
    }

    @FXML
    public void initialize(){
        bugListView.setCellFactory(param -> new BugCell());
        bugListView.setItems(bugsModel);
        initModel();
    }

    @Override
    public void newBugAdded(Bug bug) throws ServiceException, RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugsModel.add(bug);
            }
        });
    }

    @Override
    public void newBugUpdate(Bug bug) throws ServiceException, RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugsModel.forEach(b -> {
                    if(b.getId().equals(bug.getId())) {
                        b.setName(bug.getName());
                        b.setDescription(bug.getDescription());
                        b.setPriority(bug.getPriority());
                    }
                });
            }
        });
    }


    void logoutVerifier() {
        try {
            server.logoutVerifier(verifier, this);
        } catch (ServiceException e) {
            System.out.println("Logout error " + e);
        }
    }

    void logoutProgrammer(){
        try {
            server.logoutProgrammer(programmer, this);
        } catch (ServiceException e) {
            System.out.println("Logout error " + e);
        }
    }


    public void initModel() {
        try {
            bugsModel.addAll(server.getBugs());
//            requestModel.addAll(server.getAllCompetitors());
        } catch (ServiceException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleAddBug(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/AddBugView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Bug register");
            stage.setScene(new Scene(root));
            AddBugController addBugController = loader.getController();
            addBugController.setServer(server);
            stage.show();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void handleUpdateBug(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/UpdateBugView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Bug register");
            stage.setScene(new Scene(root));
            UpdateBugController updateBugControler = loader.getController();
            updateBugControler.setServer(server);

            Bug bug = bugListView.getSelectionModel().getSelectedItem();
            if(bug != null) {
                updateBugControler.setParams(bug.getId(), bug.getName(), bug.getDescription(), bug.getPriority());
                stage.show();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("No bug selected");
                errorAlert.setContentText("You must select one bug first.");
                errorAlert.showAndWait();
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    void sortByPriority(){
        bugsModel.clear();
        bugsModel.addAll(server.sortBugsByPriority());
    }

    @FXML
    private void handleLogout(){
        if(verifier != null) {
            logoutVerifier();
        } else {
            logoutProgrammer();
        }
        System.exit(0);
    }


}
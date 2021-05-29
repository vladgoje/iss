package gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import model.*;
import services.IObserver;
import services.IService;
import services.ServiceException;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ResourceBundle;

public class MainProgrammerController extends UnicastRemoteObject implements Serializable, Initializable, IObserver {
    /************BUG CELL****************/
    static class BugCell extends ListCell<Bug> {
        HBox hbox = new HBox();
        Label name = new Label("(empty)");
        Label description = new Label("(empty)");
        Label priority = new Label("(empty)");
        Label status = new Label("(empty)");
        Pane pane1 = new Pane();
        Pane pane2= new Pane();
        Pane pane3 = new Pane();
        Bug lastItem;

        public BugCell() {
            super();
            pane1.setMinWidth(2);
            pane2.setMinWidth(2);
            pane3.setMinWidth(2);

            this.setStyle("-fx-cursor: hand");

            hbox.getChildren().addAll(name, pane1, description, pane2, priority, pane3, status);
            hbox.setAlignment(Pos.CENTER);
            HBox.setHgrow(pane1, Priority.ALWAYS);
            HBox.setHgrow(pane2, Priority.ALWAYS);
            HBox.setHgrow(pane3, Priority.ALWAYS);
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
                    status.setText(item.getStatus().toString());
                }
                setGraphic(hbox);
            }
        }
    }

    /************REQUEST CELL****************/
    static class RequestCell extends ListCell<VerificationRequest> {
        HBox hbox = new HBox();
        Label programmer = new Label("(empty)");
        Label bug = new Label("(empty)");
        Label status = new Label("(empty)");
        Pane pane1 = new Pane();
        Pane pane2= new Pane();
        VerificationRequest lastItem;

        public RequestCell() {
            super();
            pane1.setMinWidth(2);
            pane2.setMinWidth(2);

            this.setStyle("-fx-cursor: hand");

            hbox.getChildren().addAll(programmer, pane1, bug, pane2, status);
            hbox.setAlignment(Pos.CENTER);
            HBox.setHgrow(pane1, Priority.ALWAYS);
            HBox.setHgrow(pane2, Priority.ALWAYS);
        }

        @Override
        protected void updateItem(VerificationRequest item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                if(item != null){
                    programmer.setText(item.getProgrammer().getUsername());
                    bug.setText("BUG: " + item.getBug().getName());
                    status.setText(item.getStatus().toString());
                }
                setGraphic(hbox);
            }
        }
    }

    private IService server;

    Programmer programmer;

    ObservableList<Bug> bugsModel = FXCollections.observableArrayList();
    ObservableList<VerificationRequest> requestModel = FXCollections.observableArrayList();

    @FXML
    Label connectedLabel;
    @FXML
    ListView<Bug> bugListView;
    @FXML
    ListView<VerificationRequest> requestListView;
    @FXML
    Label fixedBugs;
    @FXML
    Label score;

    public MainProgrammerController() throws RemoteException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setServer(IService s){
        server=s;
    }

    public void setConnectedProgrammer(Programmer programmer) {
        this.programmer = programmer;
        connectedLabel.setText("Connected programmer: " + programmer.getUsername());
        fixedBugs.setText("Fixed bugs: " + programmer.getFixedBugs());
        score.setText("Your score: " + programmer.getScore());
    }

    @FXML
    public void initialize() throws ServiceException {
        bugListView.setCellFactory(param -> new BugCell());
        bugListView.setItems(bugsModel);
        requestListView.setCellFactory(param -> new RequestCell());
        requestListView.setItems(requestModel);
        initModel();
        server.addObserver(programmer.getUsername(), this);
    }

    @Override
    public void newBugAdded(Bug bug) throws RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugsModel.add(bug);
            }
        });
    }

    @Override
    public void newBugUpdate(Bug bug) throws RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugsModel.clear();
                bugsModel.addAll(server.getProgrammerBugs(programmer.getId()));
                bugListView.refresh();
            }
        });
    }

    @Override
    public void newBugPending(Bug bug, Programmer programmer) throws RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                bugsModel.clear();
                bugsModel.addAll(server.getProgrammerBugs(programmer.getId()));
                bugListView.refresh();
            }
        });
    }

    @Override
    public void newRequestAdded(VerificationRequest request) throws RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if(request.getProgrammer().getId().equals(programmer.getId())){
                    requestModel.add(request);
                }
            }
        });
    }

    @Override
    public void newRequestUpdate(VerificationRequest request) throws RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                requestModel.forEach(r -> {
                    if(r.getId().equals(request.getId())) {
                        r.setStatus(request.getStatus());
                    }
                });
                requestListView.refresh();
                if(programmer.getId().equals(request.getProgrammer().getId())) {
                    fixedBugs.setText("Fixed bugs: " + request.getProgrammer().getFixedBugs());
                    score.setText("Your score: " + request.getProgrammer().getScore());
                }
            }
        });
    }


    void logout() {
        try {
            server.logout(programmer, this);
        } catch (ServiceException e) {
            System.out.println("Logout error " + e);
        }
    }


    public void initModel() {
        bugsModel.addAll(server.getProgrammerBugs(programmer.getId()));
        requestModel.addAll(server.getProgrammerRequests(programmer.getId()));
    }

    @FXML
    void sortByPriority(){
        bugsModel.clear();
        bugsModel.addAll(server.sortBugsByPriorityProgrammer(programmer.getId()));
    }

    @FXML
    void handleFixBug(){
        Bug bug = bugListView.getSelectionModel().getSelectedItem();
        if(bug != null) {
            if(bug.getStatus().equals(BugStatus.ACTIVE)) {
                bug.setStatus(BugStatus.PENDING);
                try{
                    server.addBugSolver(new BugSolver(bug.getId(), programmer.getId()));
                    Bug modified = server.updateBug(bug);
                    if(modified == null){
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Update bug error");
                        alert.setHeaderText("DB error");
                        alert.setContentText("Error updating bug");
                        alert.show();
                    }
                } catch (ServiceException e){
                    e.printStackTrace();
                }
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                errorAlert.setHeaderText("Wrong bug selected");
                errorAlert.setContentText("You must select an ACTIVE bug");
                errorAlert.showAndWait();
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setHeaderText("No bug selected");
            errorAlert.setContentText("You must select one bug first.");
            errorAlert.showAndWait();
        }
    }

    @FXML
    void sendRequest(){
        Bug bug = bugListView.getSelectionModel().getSelectedItem();
        if(bug != null) {
            if(bug.getStatus().equals(BugStatus.PENDING)) {
                VerificationRequest request = new VerificationRequest(bug, programmer, RequestStatus.PENDING);
                VerificationRequest sent = server.sendRequest(request);
                if(sent == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Update bug error");
                    alert.setHeaderText("DB error");
                    alert.setContentText("Error updating bug");
                    alert.show();
                }
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                errorAlert.setHeaderText("Wrong bug selected");
                errorAlert.setContentText("You must select an ACTIVE bug");
                errorAlert.showAndWait();
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setHeaderText("No bug selected");
            errorAlert.setContentText("You must select one bug first.");
            errorAlert.showAndWait();
        }
    }

    @FXML
    public void openRankList(){
        try{
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/RankListView.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Rank List");
            stage.setScene(new Scene(root));
            RankListController rankListController = loader.getController();
            rankListController.setServer(server);
            rankListController.initialize();
            stage.show();
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(){
        logout();
        System.exit(0);
    }


}
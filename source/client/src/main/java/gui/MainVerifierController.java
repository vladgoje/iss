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

public class MainVerifierController extends UnicastRemoteObject implements Serializable, Initializable, IObserver {
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

    Verifier verifier;

    ObservableList<Bug> bugsModel = FXCollections.observableArrayList();
    ObservableList<VerificationRequest> requestModel = FXCollections.observableArrayList();

    @FXML
    Label connectedLabel;
    @FXML
    ListView<Bug> bugListView;
    @FXML
    ListView<VerificationRequest> requestListView;
    public MainVerifierController() throws RemoteException {
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setServer(IService s){
        server=s;
    }

    public void setConnectedVerifier(Verifier verifier) {
        this.verifier = verifier;
        connectedLabel.setText("Connected verifier: " + verifier.getUsername());
    }

    @FXML
    public void initialize() throws ServiceException {
        bugListView.setCellFactory(param -> new BugCell());
        bugListView.setItems(bugsModel);
        requestListView.setCellFactory(param -> new RequestCell());
        requestListView.setItems(requestModel);
        initModel();
        server.addObserver(verifier.getUsername(), this);
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
                bugsModel.forEach(b -> {
                    if (b.getId().equals(bug.getId())) {
                        b.setName(bug.getName());
                        b.setDescription(bug.getDescription());
                        b.setPriority(bug.getPriority());
                        b.setStatus(bug.getStatus());
                    }
                });
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
                bugsModel.addAll(server.getBugs());
                bugListView.refresh();
            }
        });
    }

    @Override
    public void newRequestAdded(VerificationRequest request) throws RemoteException {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                requestModel.add(request);
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
            }
        });
    }


    void logout() {
        try {
            server.logout(verifier, this);
        } catch (ServiceException e) {
            System.out.println("Logout error " + e);
        }
    }


    public void initModel() {
        bugsModel.addAll(server.getBugs());
        requestModel.addAll(server.getAllRequests());
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
                updateBugControler.setParams(bug.getId(), bug.getName(), bug.getDescription(), bug.getPriority(), bug.getStatus());
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
    void showAll(){
        bugsModel.clear();
        bugsModel.addAll(server.getBugs());
    }

    @FXML
    void showActive(){
        bugsModel.clear();
        bugsModel.addAll(server.getActiveBugs());
    }

    @FXML
    void showPending(){
        bugsModel.clear();
        bugsModel.addAll(server.getPendingBugs());
    }

    @FXML
    void showSolved(){
        bugsModel.clear();
        bugsModel.addAll(server.getSolvedBugs());
    }

    @FXML
    public void acceptVerificationRequest(){
        updateVerificationRequest(RequestStatus.ACCEPTED);
    }

    @FXML
    public void declineVerificationRequest(){
        updateVerificationRequest(RequestStatus.DECLINED);
    }

    public void updateVerificationRequest(RequestStatus status){
        VerificationRequest request = requestListView.getSelectionModel().getSelectedItem();
        if(request != null){
            if(request.getStatus().equals(RequestStatus.PENDING)) {
                request.setStatus(status);
                VerificationRequest modified = server.updateRequest(request);
                if(modified == null){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Update request error");
                    alert.setHeaderText("DB error");
                    alert.setContentText("Error updating request");
                    alert.show();
                } else {
                    server.removeBugSolver(new BugSolver(request.getBug().getId(), request.getProgrammer().getId()));
                }
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                errorAlert.setHeaderText("Wrong request selected");
                errorAlert.setContentText("You must select an PENDING request to accept");
                errorAlert.showAndWait();
            }
        } else {
            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
            errorAlert.setHeaderText("No request selected");
            errorAlert.setContentText("You must select one request first.");
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
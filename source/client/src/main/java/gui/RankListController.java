package gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import model.Bug;
import model.BugPriority;
import model.Programmer;
import services.IService;

import java.io.Serializable;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ResourceBundle;

public class RankListController  extends UnicastRemoteObject implements Serializable, Initializable {
    /************Programmer CELL****************/
    static class ProgrammerCell extends ListCell<Programmer> {
        HBox hbox = new HBox();
        Label username = new Label("(empty)");
        Label fixedBugs = new Label("(empty)");
        Label score = new Label("(empty)");
        Pane pane1 = new Pane();
        Pane pane2= new Pane();
        Programmer lastItem;

        public ProgrammerCell() {
            super();
            pane1.setMinWidth(2);
            pane2.setMinWidth(2);

            this.setStyle("-fx-cursor: hand");

            hbox.getChildren().addAll(username, pane1, fixedBugs, pane2, score);
            hbox.setAlignment(Pos.CENTER);
            HBox.setHgrow(pane1, Priority.ALWAYS);
            HBox.setHgrow(pane2, Priority.ALWAYS);
        }

        @Override
        protected void updateItem(Programmer item, boolean empty) {
            super.updateItem(item, empty);
            setText(null);
            if (empty) {
                lastItem = null;
                setGraphic(null);
            } else {
                lastItem = item;
                if(item != null){
                    username.setText(item.getUsername());
                    fixedBugs.setText("FIXED BUGS: " + item.getFixedBugs());
                    score.setText("SCORE: " + item.getScore());
                }
                setGraphic(hbox);
            }
        }
    }

    ObservableList<Programmer> programmersModel = FXCollections.observableArrayList();

    @FXML
    ListView<Programmer> programmersListView;

    public RankListController() throws RemoteException {
    }

    private IService server;

    public void setServer(IService s){
        server = s;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void initialize(){
        programmersListView.setCellFactory(param -> new ProgrammerCell());
        programmersListView.setItems(programmersModel);
        programmersModel.addAll(server.getProgrammers());
    }

}

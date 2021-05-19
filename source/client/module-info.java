module Client{
    requires javafx.fxml;
    requires javafx.controls;
    opens client to javafx.graphics;
    exports client;
}
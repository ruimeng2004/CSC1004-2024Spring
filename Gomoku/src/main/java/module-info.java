module com.example.newfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.newfx to javafx.fxml;
    exports com.example.newfx;
}
module root.visuald {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens root.visuald to javafx.fxml;
    exports root.visuald;
    exports controller;
    opens controller to javafx.fxml;
}
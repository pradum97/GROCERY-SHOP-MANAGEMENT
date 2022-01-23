module com.grocery.management {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.commons.io;
    requires jasperreports;

    opens com.grocery.management to javafx.fxml;
    exports com.grocery.management;
    exports com.grocery.management.Model;
    opens com.grocery.management.Model to javafx.fxml;
    exports com.grocery.management.Controller;
    opens com.grocery.management.Controller to javafx.fxml;
    exports com.grocery.management.Method;
    opens com.grocery.management.Method to javafx.fxml;
}
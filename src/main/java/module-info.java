module com.fiction {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive javafx.graphics;
    requires transitive java.sql;

    opens com.fiction to javafx.fxml;
    exports com.fiction;
}

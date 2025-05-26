module com.mycompany.todo {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.mycompany.todo to javafx.fxml;
    exports com.mycompany.todo;
}

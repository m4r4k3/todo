module com.mycompany.todo {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.mycompany.todo;
    exports com.mycompany.todo.view to javafx.fxml;

    opens com.mycompany.todo.view to javafx.fxml;  // add this line
}

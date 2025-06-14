package com.mycompany.todo;

import com.mycompany.todo.Util.EntityManager;
import com.mycompany.todo.Util.ViewManager;
import javafx.application.Application;
import javafx.stage.Stage;


/**
 * JavaFX App
 */
public class App extends Application {

@Override
public void start(Stage stage) {
    ViewManager.setStage(stage);
   ViewManager.switchTo("/com/mycompany/todo/Views/TaskView.fxml", 640, 480);
}

    public static void main(String[] args) {
        launch();
    }

}
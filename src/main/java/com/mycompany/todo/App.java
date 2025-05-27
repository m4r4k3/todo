package com.mycompany.todo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
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
package com.mycompany.todo.view;

import com.mycompany.todo.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTaskFormView {

    @FXML
    private TextField titleInput;

    @FXML
    private TextArea descriptionInput;

    private TasksView tasksView;

    public void setTasksView(TasksView tasksView) {
        this.tasksView = tasksView;
    }

    @FXML
    private void handleSaveTask() {
        String title = titleInput.getText().trim();
        String description = descriptionInput.getText().trim();

        if (!title.isEmpty()) {
            Task newTask = new Task(title, description);
            tasksView.addTask(newTask);
            closeWindow();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleInput.getScene().getWindow();
        stage.close();
    }
}
package com.mycompany.todo.view;

import com.mycompany.todo.controller.TaskController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AddTaskFormView {

    @FXML
    private TextField titleInput;

    @FXML
    private TextArea descriptionInput;

    private TaskController taskController;
    private TasksView tasksView;

    @FXML
    private void initialize() {
        taskController = TaskController.getInstance();
        // Focus on title input when form opens
        titleInput.requestFocus();
    }

    public void setTasksView(TasksView tasksView) {
        this.tasksView = tasksView;
    }

    @FXML
    private void handleSaveTask() {
        String title = titleInput.getText().trim();
        String description = descriptionInput.getText().trim();

        try {
            // Create new task using the controller
            taskController.createTask(title, description.isEmpty() ? null : description);

            // Update the main view
            if (tasksView != null) {
                tasksView.refreshView();
            }

            // Show success message
            taskController.showAlert("Succès", "Tâche créée avec succès!", Alert.AlertType.INFORMATION);

            // Close the form
            closeWindow();

        } catch (IllegalArgumentException e) {
            // Handle validation errors
            taskController.showAlert("Erreur", e.getMessage(), Alert.AlertType.WARNING);
            titleInput.requestFocus();
        } catch (Exception e) {
            // Handle other errors
            taskController.showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
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
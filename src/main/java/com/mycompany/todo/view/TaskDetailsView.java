package com.mycompany.todo.view;

import com.mycompany.todo.controller.TaskController;
import com.mycompany.todo.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TaskDetailsView {

    @FXML private TextField titleInput;
    @FXML private TextArea descriptionInput;
    @FXML private CheckBox completedCheckbox;
    @FXML private VBox dateInfoContainer;
    @FXML private Label creationDateLabel;
    @FXML private Label completionDateLabel;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private Task currentTask;
    private TaskController taskController;
    private TasksView parentView;
    private Stage currentStage;

    public void initialize() {
        taskController = TaskController.getInstance();
    }

    public void setTask(Task task) {
        this.currentTask = task;
        populateFields();
    }

    public void setParentView(TasksView parentView) {
        this.parentView = parentView;
    }

    public void setStage(Stage stage) {
        this.currentStage = stage;
    }

    private void populateFields() {
        if (currentTask == null) return;

        titleInput.setText(currentTask.getTitle());
        descriptionInput.setText(currentTask.getDescription() != null ? currentTask.getDescription() : "");
        completedCheckbox.setSelected(currentTask.isCompleted());
        updateDateLabels();
    }

    private void updateDateLabels() {
        if (currentTask == null) return;

        // Creation date
        setLabelVisibility(creationDateLabel,
                currentTask.getCreationDate() != null,
                "📅 Créé: " + currentTask.getShortFormattedCreationDate());

        // Completion date
        setLabelVisibility(completionDateLabel,
                currentTask.isCompleted() && currentTask.getCompletionDate() != null,
                "✅ Terminé: " + currentTask.getShortFormattedCompletionDate());
    }

    private void setLabelVisibility(Label label, boolean visible, String text) {
        if (visible) {
            label.setText(text);
            label.setVisible(true);
            label.setManaged(true);
        } else {
            label.setVisible(false);
            label.setManaged(false);
        }
    }

    @FXML
    private void handleUpdate() {
        if (currentTask == null) return;

        // Validate using controller
        if (!taskController.validateTaskInput(titleInput.getText(), descriptionInput.getText())) {
            return;
        }

        // Use controller's performTaskAction for consistent handling
        taskController.performTaskAction(
                () -> updateTaskData(),
                "Tâche mise à jour avec succès!",
                "Impossible de mettre à jour la tâche",
                this::refreshParentAndClose
        );
    }

    @FXML
    private void handleDelete() {
        if (currentTask == null) return;

        // Use controller's confirmation dialog
        if (taskController.showDeleteConfirmation(currentTask)) {
            taskController.performTaskAction(
                    () -> taskController.deleteTask(currentTask),
                    "Tâche supprimée avec succès!",
                    "Impossible de supprimer la tâche",
                    this::refreshParentAndClose
            );
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    /**
     * Updates the current task with form data
     */
    private void updateTaskData() throws Exception {
        // Update task properties
        currentTask.setTitle(titleInput.getText().trim());
        currentTask.setDescription(descriptionInput.getText().trim());

        // Handle completion status change
        boolean wasCompleted = currentTask.isCompleted();
        boolean isNowCompleted = completedCheckbox.isSelected();

        if (wasCompleted != isNowCompleted) {
            taskController.toggleTaskCompletion(currentTask);
        } else {
            taskController.updateTask(currentTask);
        }
    }

    /**
     * Common method to refresh parent view and close window
     */
    private void refreshParentAndClose() {
        if (parentView != null) {
            parentView.refreshView();
        }
        closeWindow();
    }

    private void closeWindow() {
        if (currentStage != null) {
            currentStage.close();
        }
    }

    public Task getCurrentTask() {
        return currentTask;
    }
}
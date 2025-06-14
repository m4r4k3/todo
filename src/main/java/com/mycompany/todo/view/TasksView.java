package com.mycompany.todo.view;

import com.mycompany.todo.controller.TaskController;
import com.mycompany.todo.model.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class TasksView {

    @FXML
    private ListView<Task> taskList;

    @FXML
    private TextField searchInput;

    @FXML
    private ComboBox<String> filterComboBox;

    private TaskController taskController;
    private ObservableList<Task> allTasks;
    private ObservableList<Task> filteredTasks;
    private TaskController.FilterType currentFilter = TaskController.FilterType.UNCOMPLETED;

    @FXML
    public void initialize() {
        taskController = TaskController.getInstance();

        // Load all tasks from database
        refreshTasksFromDatabase();

        // Setup UI components
        setupTaskList();
        setupFilterComboBox();
        setupPlaceholder();

        // Apply initial filters
        applyFilters();
    }

    private void refreshTasksFromDatabase() {
        allTasks = taskController.getAllTasks();
    }

    private void setupTaskList() {
        taskList.setCellFactory(param -> new TaskListCell());
    }

    private void setupFilterComboBox() {
        filterComboBox.setItems(taskController.getFilterLabels());
        filterComboBox.setValue("Non terminées");
    }

    private void setupPlaceholder() {
        Label placeholder = new Label("Aucune tâche trouvée");
        placeholder.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-text-fill: #a0aec0; " +
                        "-fx-font-family: 'SF Pro Text', 'Segoe UI', system-ui;"
        );
        taskList.setPlaceholder(placeholder);
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleFilterChange() {
        String selectedFilter = filterComboBox.getValue();
        currentFilter = taskController.getFilterTypeFromLabel(selectedFilter);
        applyFilters();
    }

    private void applyFilters() {
        String searchQuery = searchInput.getText();
        filteredTasks = taskController.filterTasks(allTasks, currentFilter, searchQuery);
        taskList.setItems(filteredTasks);
    }

    @FXML
    private void handleShowAddTaskForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/todo/Views/TaskForm.fxml"));
            Parent root = loader.load();

            AddTaskFormView controller = loader.getController();
            controller.setTasksView(this);

            Stage stage = new Stage();
            stage.setTitle("Add New Task");
            stage.setScene(new Scene(root, 400, 350));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();
        } catch (IOException e) {
            taskController.showAlert("Erreur", "Impossible d'ouvrir le formulaire: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    /**
     * Shows the task details window for the given task
     * @param task The task to show details for
     */
    private void showTaskDetails(Task task) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/todo/Views/TaskDetails.fxml"));
            Parent root = loader.load();

            TaskDetailsView controller = loader.getController();
            controller.setTask(task);
            controller.setParentView(this);

            Stage stage = new Stage();
            stage.setTitle("Détails de la tâche");
            stage.setScene(new Scene(root, 450, 420));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);

            // Set the stage reference in the controller so it can close itself
            controller.setStage(stage);

            stage.showAndWait();
        } catch (IOException e) {
            taskController.showAlert("Erreur", "Impossible d'ouvrir les détails de la tâche: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    public void refreshView() {
        refreshTasksFromDatabase();
        applyFilters();
    }

    // Enhanced Custom ListCell class for displaying tasks with checkboxes and dates
    private class TaskListCell extends ListCell<Task> {
        private VBox content;
        private HBox mainContent;
        private CheckBox checkBox;
        private VBox textContainer;
        private Label titleLabel;
        private Label descriptionLabel;
        private Label dateLabel;

        public TaskListCell() {
            super();
            setupUI();
            setupEventHandlers();
        }

        private void setupUI() {
            // Create components
            checkBox = new CheckBox();
            titleLabel = new Label();
            descriptionLabel = new Label();
            dateLabel = new Label();

            // Create text container
            textContainer = new VBox(2);
            textContainer.getChildren().addAll(titleLabel, descriptionLabel, dateLabel);

            // Create main horizontal content
            mainContent = new HBox(12);
            mainContent.setAlignment(Pos.CENTER_LEFT);
            mainContent.getChildren().addAll(checkBox, textContainer);
            HBox.setHgrow(textContainer, Priority.ALWAYS);

            // Create main vertical container
            content = new VBox();
            content.getChildren().add(mainContent);
            content.setPadding(new Insets(8, 12, 8, 12));

            // Apply styles
            setupStyles();
        }

        private void setupStyles() {
            // Title label styles
            titleLabel.setStyle(
                    "-fx-font-size: 14px; " +
                            "-fx-font-weight: 600; " +
                            "-fx-text-fill: #2d3748; " +
                            "-fx-font-family: 'SF Pro Text', 'Segoe UI', system-ui;"
            );

            // Description label styles
            descriptionLabel.setStyle(
                    "-fx-font-size: 12px; " +
                            "-fx-font-weight: 400; " +
                            "-fx-text-fill: #718096; " +
                            "-fx-font-family: 'SF Pro Text', 'Segoe UI', system-ui;"
            );

            // Date label styles
            dateLabel.setStyle(
                    "-fx-font-size: 11px; " +
                            "-fx-font-weight: 400; " +
                            "-fx-text-fill: #a0aec0; " +
                            "-fx-font-family: 'SF Pro Text', 'Segoe UI', system-ui;"
            );

            // Content container styles
            content.setStyle(
                    "-fx-background-color: #ffffff; " +
                            "-fx-background-radius: 8; " +
                            "-fx-border-color: #e2e8f0; " +
                            "-fx-border-width: 1; " +
                            "-fx-border-radius: 8; " +
                            "-fx-cursor: hand;"
            );
        }

        private void setupEventHandlers() {
            checkBox.setOnAction(event -> {
                Task task = getItem();
                if (task != null) {
                    try {
                        taskController.toggleTaskCompletion(task);
                        updateTaskDisplay(task);
                        applyFilters();
                    } catch (Exception e) {
                        taskController.showAlert("Erreur",
                                "Impossible de mettre à jour la tâche: " + e.getMessage(),
                                Alert.AlertType.ERROR);
                        // Revert checkbox state
                        checkBox.setSelected(task.isCompleted());
                    }
                }
            });

            // Add click handler for the entire cell (excluding checkbox)
            content.setOnMouseClicked(event -> {
                if (!event.isConsumed() && getItem() != null) {
                    // Check if click was not on checkbox area
                    if (event.getX() > 30) { // Approximate checkbox width + margin
                        showTaskDetails(getItem());
                    }
                }
            });

            // Prevent text container clicks from being consumed by checkbox
            textContainer.setOnMouseClicked(event -> {
                if (getItem() != null) {
                    showTaskDetails(getItem());
                    event.consume();
                }
            });
        }

        @Override
        protected void updateItem(Task task, boolean empty) {
            super.updateItem(task, empty);
            if (empty || task == null) {
                setGraphic(null);
            } else {
                checkBox.setSelected(task.isCompleted());
                updateTaskDisplay(task);
                setGraphic(content);
            }
        }

        private void updateTaskDisplay(Task task) {
            // Update title
            titleLabel.setText(task.getTitle());

            // Update description
            String description = task.getDescription();
            if (description != null && !description.trim().isEmpty()) {
                descriptionLabel.setText(description);
                descriptionLabel.setVisible(true);
                descriptionLabel.setManaged(true);
            } else {
                descriptionLabel.setVisible(false);
                descriptionLabel.setManaged(false);
            }

            // Update date information
            updateDateDisplay(task);

            // Update styles based on completion status
            if (task.isCompleted()) {
                titleLabel.setStyle(
                        "-fx-font-size: 14px; " +
                                "-fx-font-weight: 600; " +
                                "-fx-text-fill: #a0aec0; " +
                                "-fx-font-family: 'SF Pro Text', 'Segoe UI', system-ui; " +
                                "-fx-strikethrough: true;"
                );
                descriptionLabel.setStyle(
                        "-fx-font-size: 12px; " +
                                "-fx-font-weight: 400; " +
                                "-fx-text-fill: #a0aec0; " +
                                "-fx-font-family: 'SF Pro Text', 'Segoe UI', system-ui; " +
                                "-fx-strikethrough: true;"
                );
                content.setStyle(
                        "-fx-background-color: #f7fafc; " +
                                "-fx-background-radius: 8; " +
                                "-fx-border-color: #e2e8f0; " +
                                "-fx-border-width: 1; " +
                                "-fx-border-radius: 8; " +
                                "-fx-cursor: hand;"
                );
            } else {
                // Reset to normal styles
                setupStyles();
            }
        }

        private void updateDateDisplay(Task task) {
            StringBuilder dateText = new StringBuilder();

            if (task.getCreationDate() != null) {
                dateText.append("📅 Créé: ").append(task.getShortFormattedCreationDate());
            }

            if (task.isCompleted() && task.getCompletionDate() != null) {
                if (dateText.length() > 0) {
                    dateText.append(" • ");
                }
                dateText.append("✅ Terminé: ").append(task.getShortFormattedCompletionDate());
            }

            if (dateText.length() > 0) {
                dateLabel.setText(dateText.toString());
                dateLabel.setVisible(true);
                dateLabel.setManaged(true);
            } else {
                dateLabel.setVisible(false);
                dateLabel.setManaged(false);
            }
        }
    }
}
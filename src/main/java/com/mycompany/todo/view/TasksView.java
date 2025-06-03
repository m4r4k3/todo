package com.mycompany.todo.view;

import com.mycompany.todo.Util.EntityManager;
import com.mycompany.todo.model.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;

public class TasksView {

    @FXML
    private ListView<Task> taskList;

    @FXML
    private TextField searchInput;

    @FXML
    private ComboBox<String> filterComboBox;

    private ObservableList<Task> allTasks = FXCollections.observableArrayList();
    private ObservableList<Task> filteredTasks = FXCollections.observableArrayList();

    public enum FilterType {
        ALL, COMPLETED, UNCOMPLETED
    }

    private FilterType currentFilter = FilterType.UNCOMPLETED;

    @FXML
    public void initialize() {
        var em = EntityManager.getEntityManager();
        allTasks.setAll(em.createQuery("SELECT t FROM Task t", Task.class).getResultList());
        em.close();

        taskList.setItems(filteredTasks);
        // Setup filter combo box with French labels
        filterComboBox.setItems(FXCollections.observableArrayList("Tout", "Terminées", "Non terminées"));
        filterComboBox.setValue("Non terminées");

        taskList.setCellFactory(param -> new TaskListCell());

        applyFilters();
    }


    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleFilterChange() {
        String selectedFilter = filterComboBox.getValue();
        switch (selectedFilter) {
            case "Tout":
                currentFilter = FilterType.ALL;
                break;
            case "Terminées":
                currentFilter = FilterType.COMPLETED;
                break;
            case "Non terminées":
                currentFilter = FilterType.UNCOMPLETED;
                break;
        }
        applyFilters();
    }

    private void applyFilters() {
        String searchQuery = searchInput.getText();
        filteredTasks.clear();

        for (Task task : allTasks) {
            boolean matchesSearch = searchQuery == null || searchQuery.trim().isEmpty() || task.matches(searchQuery);
            boolean matchesFilter = false;

            switch (currentFilter) {
                case ALL:
                    matchesFilter = true;
                    break;
                case COMPLETED:
                    matchesFilter = task.isCompleted();
                    break;
                case UNCOMPLETED:
                    matchesFilter = !task.isCompleted();
                    break;
            }

            if (matchesSearch && matchesFilter) {
                filteredTasks.add(task);
            }
        }
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
            e.printStackTrace();
        }
    }

    public void addTask(Task task) {
        allTasks.add(task);
        applyFilters(); // Refresh the filtered list
    }

    public void refreshView() {
        applyFilters();
    }

    // Custom ListCell class for displaying tasks with checkboxes
    private class TaskListCell extends ListCell<Task> {
        private HBox content;
        private CheckBox checkBox;
        private Label taskLabel;

        public TaskListCell() {
            super();
            checkBox = new CheckBox();
            taskLabel = new Label();
            content = new HBox(10);
            content.getChildren().addAll(checkBox, taskLabel);
            content.setStyle("-fx-alignment: center-left; -fx-padding: 5;");

            checkBox.setOnAction(event -> {
                Task task = getItem();
                if (task != null) {
                    task.setCompleted(checkBox.isSelected());

                    // Persist update in DB
                    var em = EntityManager.getEntityManager();
                    em.getTransaction().begin();
                    em.merge(task); // Use merge to update existing entity
                    em.getTransaction().commit();
                    em.close();

                    updateTaskAppearance();
                    applyFilters();
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
                taskLabel.setText(task.getTitle());
                updateTaskAppearance();
                setGraphic(content);
            }
        }

        private void updateTaskAppearance() {
            Task task = getItem();
            if (task != null && task.isCompleted()) {
                taskLabel.setStyle("-fx-text-fill: #888; -fx-strikethrough: true;");
            } else {
                taskLabel.setStyle("-fx-text-fill: #2c3e50;");
            }
        }
    }
}
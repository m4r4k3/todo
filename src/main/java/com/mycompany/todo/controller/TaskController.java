package com.mycompany.todo.controller;

import com.mycompany.todo.Util.EntityManager;
import com.mycompany.todo.model.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TaskController {

    public enum FilterType {
        ALL, COMPLETED, UNCOMPLETED, RECENT_CREATED, RECENT_COMPLETED
    }

    private static TaskController instance;

    private TaskController() {}

    public static TaskController getInstance() {
        if (instance == null) {
            instance = new TaskController();
        }
        return instance;
    }

    /**
     * Retrieves all tasks from the database ordered by creation date (newest first)
     */
    public ObservableList<Task> getAllTasks() {
        var em = EntityManager.getEntityManager();
        try {
            List<Task> tasks = em.createQuery("SELECT t FROM Task t ORDER BY t.creationDate DESC", Task.class)
                    .getResultList();
            return FXCollections.observableArrayList(tasks);
        } finally {
            em.close();
        }
    }

    /**
     * Creates a new task and persists it to the database
     */
    public Task createTask(String title, String description) throws Exception {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de la tâche est obligatoire.");
        }

        Task newTask = new Task(title.trim(),
                description != null && !description.trim().isEmpty() ? description.trim() : null);

        var em = EntityManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(newTask);
            em.getTransaction().commit();
            return newTask;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Erreur lors de la création de la tâche: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Updates an existing task in the database
     */
    public void updateTask(Task task) throws Exception {
        var em = EntityManager.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(task);
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Erreur lors de la mise à jour de la tâche: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Toggles the completion status of a task
     */
    public void toggleTaskCompletion(Task task) throws Exception {
        task.setCompleted(!task.isCompleted());
        updateTask(task);
    }

    /**
     * Deletes a task from the database
     */
    public void deleteTask(Task task) throws Exception {
        var em = EntityManager.getEntityManager();
        try {
            em.getTransaction().begin();
            Task managedTask = em.find(Task.class, task.getId());
            if (managedTask != null) {
                em.remove(managedTask);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new Exception("Erreur lors de la suppression de la tâche: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    /**
     * Validates task input fields
     * @param title The task title
     * @param description The task description
     * @return true if valid, false otherwise
     */
    public boolean validateTaskInput(String title, String description) {
        if (title == null || title.trim().isEmpty()) {
            showAlert("Erreur de validation", "Le titre est obligatoire.", Alert.AlertType.WARNING);
            return false;
        }

        if (title.trim().length() > 255) {
            showAlert("Erreur de validation", "Le titre ne peut pas dépasser 255 caractères.", Alert.AlertType.WARNING);
            return false;
        }

        if (description != null && description.trim().length() > 1000) {
            showAlert("Erreur de validation", "La description ne peut pas dépasser 1000 caractères.", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    /**
     * Shows a confirmation dialog for task deletion
     * @param task The task to be deleted
     * @return true if user confirms deletion, false otherwise
     */
    public boolean showDeleteConfirmation(Task task) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmer la suppression");
        confirmAlert.setHeaderText("Supprimer la tâche");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette tâche ?\n\n\"" +
                task.getTitle() + "\"\n\nCette action ne peut pas être annulée.");

        // Style the confirmation dialog
        confirmAlert.getDialogPane().setStyle("-fx-font-family: 'SF Pro Text', 'Segoe UI', system-ui;");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Handles successful task actions with callback
     * @param successMessage The message to display
     * @param onSuccess Callback to execute after showing success message
     */
    public void handleTaskActionSuccess(String successMessage, Runnable onSuccess) {
        if (onSuccess != null) {
            onSuccess.run();
        }
        showAlert("Succès", successMessage, Alert.AlertType.INFORMATION);
    }

    /**
     * Handles task action errors consistently
     * @param baseMessage The base error message
     * @param exception The exception that occurred
     */
    public void handleTaskActionError(String baseMessage, Exception exception) {
        showAlert("Erreur", baseMessage + ": " + exception.getMessage(), Alert.AlertType.ERROR);
        exception.printStackTrace();
    }

    /**
     * Performs a task action with consistent error handling
     * @param action The action to perform
     * @param successMessage Success message to display
     * @param errorMessage Base error message for failures
     * @param onSuccess Callback for successful completion
     */
    public void performTaskAction(TaskAction action, String successMessage, String errorMessage, Runnable onSuccess) {
        try {
            action.execute();
            handleTaskActionSuccess(successMessage, onSuccess);
        } catch (Exception e) {
            handleTaskActionError(errorMessage, e);
        }
    }

    /**
     * Functional interface for task actions
     */
    @FunctionalInterface
    public interface TaskAction {
        void execute() throws Exception;
    }

    /**
     * Filters tasks based on the given criteria
     */
    public ObservableList<Task> filterTasks(ObservableList<Task> allTasks, FilterType filterType, String searchQuery) {
        ObservableList<Task> filteredTasks = FXCollections.observableArrayList();

        for (Task task : allTasks) {
            boolean matchesSearch = matchesSearchQuery(task, searchQuery);
            boolean matchesFilter = matchesFilter(task, filterType);

            if (matchesSearch && matchesFilter) {
                filteredTasks.add(task);
            }
        }

        return filteredTasks;
    }

    /**
     * Checks if a task matches the search query
     */
    private boolean matchesSearchQuery(Task task, String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return true;
        }
        return task.matches(searchQuery);
    }

    /**
     * Checks if a task matches the filter criteria
     */
    private boolean matchesFilter(Task task, FilterType filterType) {
        switch (filterType) {
            case ALL:
                return true;
            case COMPLETED:
                return task.isCompleted();
            case UNCOMPLETED:
                return !task.isCompleted();
            case RECENT_CREATED:
                return task.getCreationDate() != null &&
                        task.getCreationDate().isAfter(LocalDateTime.now().minusDays(7));
            case RECENT_COMPLETED:
                return task.isCompleted() &&
                        task.getCompletionDate() != null &&
                        task.getCompletionDate().isAfter(LocalDateTime.now().minusDays(7));
            default:
                return false;
        }
    }

    /**
     * Shows an alert dialog with the specified parameters
     */
    public void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Gets the filter type from its French label
     */
    public FilterType getFilterTypeFromLabel(String label) {
        switch (label) {
            case "Tout":
                return FilterType.ALL;
            case "Terminées":
                return FilterType.COMPLETED;
            case "Non terminées":
                return FilterType.UNCOMPLETED;
            case "Récemment créées":
                return FilterType.RECENT_CREATED;
            case "Récemment terminées":
                return FilterType.RECENT_COMPLETED;
            default:
                return FilterType.UNCOMPLETED;
        }
    }

    /**
     * Gets all available filter labels in French
     */
    public ObservableList<String> getFilterLabels() {
        return FXCollections.observableArrayList(
                "Tout",
                "Terminées",
                "Non terminées",
                "Récemment créées",
                "Récemment terminées"
        );
    }
}
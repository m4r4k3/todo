package com.mycompany.todo.model;

public class Task {
    private String title;
    private String description;
    private boolean completed;

    public Task() {
        this.completed = false;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    public Task(String title, String description, boolean completed) {
        this.title = title;
        this.description = description;
        this.completed = completed;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    // Method to check if task matches search query
    public boolean matches(String searchQuery) {
        if (searchQuery == null || searchQuery.trim().isEmpty()) {
            return true;
        }

        String query = searchQuery.toLowerCase().trim();
        return (title != null && title.toLowerCase().contains(query)) ||
                (description != null && description.toLowerCase().contains(query));
    }

    @Override
    public String toString() {
        return title + (completed ? " ✓" : "");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Task task = (Task) obj;
        return completed == task.completed &&
                (title != null ? title.equals(task.title) : task.title == null) &&
                (description != null ? description.equals(task.description) : task.description == null);
    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (completed ? 1 : 0);
        return result;
    }
}
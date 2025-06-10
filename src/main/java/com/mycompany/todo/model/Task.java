    package com.mycompany.todo.model;
    
    import jakarta.persistence.*;
    
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    
    @Entity
    @Table(name = "tasks")
    public class Task {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
    
        private String title;
        private String description;
        private boolean completed;
    
        @Column(name = "creation_date")
        private LocalDateTime creationDate;
    
        @Column(name = "completion_date")
        private LocalDateTime completionDate;
    
        public Task() {
            this.completed = false;
            this.creationDate = LocalDateTime.now();
        }
    
        public Task(String title, String description) {
            this.title = title;
            this.description = description;
            this.completed = false;
            this.creationDate = LocalDateTime.now();
        }
    
        public Task(String title, String description, boolean completed) {
            this.title = title;
            this.description = description;
            this.completed = completed;
            this.creationDate = LocalDateTime.now();
            if (completed) {
                this.completionDate = LocalDateTime.now();
            }
        }
    
        // Getters and Setters
        public Long getId() {
            return id;
        }
    
        public void setId(Long id) {
            this.id = id;
        }
    
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
            if (completed && this.completionDate == null) {
                this.completionDate = LocalDateTime.now();
            } else if (!completed) {
                this.completionDate = null;
            }
        }
    
        public LocalDateTime getCreationDate() {
            return creationDate;
        }
    
        public void setCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
        }
    
        public LocalDateTime getCompletionDate() {
            return completionDate;
        }
    
        public void setCompletionDate(LocalDateTime completionDate) {
            this.completionDate = completionDate;
        }
    
        // Formatted date methods for display
        public String getFormattedCreationDate() {
            if (creationDate == null) return "";
            return creationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    
        public String getFormattedCompletionDate() {
            if (completionDate == null) return "";
            return completionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }
    
        public String getShortFormattedCreationDate() {
            if (creationDate == null) return "";
            return creationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        }
    
        public String getShortFormattedCompletionDate() {
            if (completionDate == null) return "";
            return completionDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
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
    
        // Enhanced toString with dates
        public String toStringWithDates() {
            StringBuilder sb = new StringBuilder();
            sb.append(title);
            if (completed) {
                sb.append(" ✓");
            }
            sb.append(" (Créé: ").append(getShortFormattedCreationDate());
            if (completed && completionDate != null) {
                sb.append(", Terminé: ").append(getShortFormattedCompletionDate());
            }
            sb.append(")");
            return sb.toString();
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
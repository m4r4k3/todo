package com.mycompany.todo.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Task {
    private final StringProperty description = new SimpleStringProperty();

    public Task(String description) {
        this.description.set(description);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String desc) {
        description.set(desc);
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    @Override
    public String toString() {
        return getDescription(); // Used for display in ListView
    }
}

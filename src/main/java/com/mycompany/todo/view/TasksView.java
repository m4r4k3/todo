package com.mycompany.todo.view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TextField;

public class TasksView {

    @FXML
    private ListView<String> taskList;

    private ObservableList<String> tasks = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        taskList.setItems(tasks);
    }

    @FXML
    private TextField taskInput;

    @FXML
    private void handleAddTask() {
        String taskText = taskInput.getText().trim();
        if (!taskText.isEmpty()) {
            tasks.add(taskText);
            taskInput.clear();
        }
    }

}

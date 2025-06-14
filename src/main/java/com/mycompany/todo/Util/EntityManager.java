package com.mycompany.todo.Util;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class EntityManager {
    private static final EntityManagerFactory emf =
            Persistence.createEntityManagerFactory("TodoDB");

    public static jakarta.persistence.EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

}

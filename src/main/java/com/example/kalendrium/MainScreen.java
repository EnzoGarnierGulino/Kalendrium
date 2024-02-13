package com.example.kalendrium;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainScreen extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainScreen.class.getResource("main-view.fxml"));
        GridPane root = fxmlLoader.load();
        Scene scene = new Scene(root, 640, 480);
        stage.setTitle("Kalendrium");
        stage.getIcons().add(new Image("file:images/K.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
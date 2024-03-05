package com.example.kalendrium;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginScreen extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginScreen.class.getResource("login.fxml"));
        AnchorPane root = fxmlLoader.load();
        Scene scene = new Scene(root, 400, 300);
        stage.setTitle("Kalendrium");
        stage.setResizable(false);
        stage.getIcons().add(new Image("file:images/K.png"));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
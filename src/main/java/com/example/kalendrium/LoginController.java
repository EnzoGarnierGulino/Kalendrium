package com.example.kalendrium;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    protected ImageView logo;

    public void initialize() {
        File file = new File("images/KalendriumLogo.png");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        boolean isAuthenticated = authenticate(username, password);

        if (isAuthenticated) {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
                Parent root = fxmlLoader.load();
                Stage mainStage = new Stage();
                mainStage.setTitle("Kalendrium");
                mainStage.setScene(new Scene(root));
                mainStage.getIcons().add(new Image("file:images/K.png"));
                mainStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Login Error");
            alert.setContentText("Invalid username or password. Please try again.");
            alert.showAndWait();
        }
    }

    private boolean authenticate(String username, String password) {
        return "admin".equals(username) && "admin".equals(password);
    }
}

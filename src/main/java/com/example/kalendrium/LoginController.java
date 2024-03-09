package com.example.kalendrium;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class LoginController {
    @FXML
    private AnchorPane root;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private ImageView logo;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    protected static boolean isDarkMode = false;
    private final Color DARKMODE = Color.rgb(38, 38, 38);


    @FXML
    public void initialize() {
        File file = new File("images/KalendriumLogo.png");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        Platform.runLater(() -> {
            usernameField.requestFocus();
            usernameField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.DOWN) {
                    passwordField.requestFocus();
                }
                if (e.getCode() == KeyCode.ENTER) {
                    handleLogin();
                }
                if (e.isControlDown() && e.getCode() == KeyCode.T) {
                    switchTheme();
                }
            });
            passwordField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.UP) {
                    usernameField.requestFocus();
                }
                if (e.getCode() == KeyCode.ENTER) {
                    handleLogin();
                }
                if (e.isControlDown() && e.getCode() == KeyCode.T) {
                    switchTheme();
                }
            });
        });
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

    private void switchTheme() {
        if (isDarkMode) {
            root.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
            usernameLabel.setTextFill(Color.BLACK);
            passwordLabel.setTextFill(Color.BLACK);
        } else {
            root.setBackground(new Background(new BackgroundFill(DARKMODE, null, null)));
            usernameLabel.setTextFill(Color.WHITE);
            passwordLabel.setTextFill(Color.WHITE);
        }
        isDarkMode = !isDarkMode;
    }

    private boolean authenticate(String username, String password) {
        // TODO: Implement authentication. It was set to empty strings for testing purposes.
        return "".equals(username) && "".equals(password);
    }
}
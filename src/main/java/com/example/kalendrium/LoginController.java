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
import javafx.stage.Stage;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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
    private static final String jsonFilePath = "db/db.json";


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
            });
            passwordField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.UP) {
                    usernameField.requestFocus();
                }
                if (e.getCode() == KeyCode.ENTER) {
                    handleLogin();
                }
            });
        });
        JSONObject currentUserJson = new JSONObject();
        currentUserJson.put("currentUserId", "0");
        try (FileWriter writer = new FileWriter("db/currentUser.json")) {
            writer.write(currentUserJson.toJSONString());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String id = authenticate(username, password);
        if (id != null) {
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

    private static String authenticate(String username, String password) {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(jsonFilePath)) {
            JSONArray users = (JSONArray) parser.parse(reader);
            for (Object userObject : users) {
                JSONObject user = (JSONObject) userObject;
                String name = (String) user.get("name");
                String pass = (String) user.get("password");
                String id = (String) user.get("id");
                if (name.equals(username) && pass.equals(password)) {
                    JSONObject currentUserJson = new JSONObject();
                    currentUserJson.put("currentUserId", id);
                    try (FileWriter writer = new FileWriter("db/currentUser.json")) {
                        writer.write(currentUserJson.toJSONString());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    return id;
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
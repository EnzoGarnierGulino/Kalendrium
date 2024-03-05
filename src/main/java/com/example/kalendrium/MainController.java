package com.example.kalendrium;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

import java.io.File;

public class MainController {
    @FXML
    private ImageView logo;

    @FXML
    private TabPane tabPane;

    @FXML
    private GridPane root;

    private static boolean isDarkMode = LoginController.isDarkMode;
    private final Color DARKMODE = Color.rgb(38, 38, 38);

    public void initialize() {
        File file = new File("images/KalendriumLogo.png");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        tabPane.setTabMaxHeight(40);
        tabPane.setTabMinHeight(40);
        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newWidth = newValue.doubleValue() / 3 - 19;
            tabPane.setTabMinWidth(newWidth);
            tabPane.setTabMaxWidth(newWidth);
        });
        Platform.runLater(() -> {
            root.getScene().setOnKeyPressed(e -> {
                if (e.isControlDown() && e.getCode() == KeyCode.T) {
                    switchTheme();
                }
            });
        });
    }

    private void switchTheme() {
        if (isDarkMode) {
            root.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
        } else {
            root.setBackground(new Background(new BackgroundFill(DARKMODE, null, null)));
        }
        isDarkMode = !isDarkMode;
    }
}
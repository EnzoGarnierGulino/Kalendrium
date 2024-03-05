package com.example.kalendrium;

import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

import java.io.File;

public class MainController {
    @FXML
    private ImageView logo;

    @FXML
    private TabPane tabPane;

    @FXML
    private GridPane root;

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
    }
}
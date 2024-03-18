package com.example.kalendrium;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.File;

public class MainController {
    @FXML
    private ImageView logo;

    @FXML
    private TabPane tabPane;

    @FXML
    private GridPane root;

    @FXML
    private Rectangle rectangle;

    private static boolean isDarkMode = LoginController.isDarkMode;
    private final Color DARKMODE = Color.rgb(38, 38, 38);
    private final int TAB_MARGIN = 19;
    private final float HEIGHT_MULTIPLICATOR = 0.6f;
    private final float WIDTH_MULTIPLICATOR = 0.8f;

    public void initialize() {
        File file = new File("images/KalendriumLogo.png");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        tabPane.setTabMaxHeight(40);
        tabPane.setTabMinHeight(40);
        // Width tracker
        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newTabWidth = newValue.doubleValue() / 3 - TAB_MARGIN;
            tabPane.setTabMinWidth(newTabWidth);
            tabPane.setTabMaxWidth(newTabWidth);
            double newRectangleWidth = newValue.doubleValue() * WIDTH_MULTIPLICATOR;
            rectangle.setWidth(newRectangleWidth);
        });
        // Height tracker
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newRectangleHeight = newValue.doubleValue() * HEIGHT_MULTIPLICATOR;
            rectangle.setHeight(newRectangleHeight);
        });
        // Commands tracker
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
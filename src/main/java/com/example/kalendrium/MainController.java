package com.example.kalendrium;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import java.util.Calendar;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    private ImageView logo;

    @FXML
    private TabPane tabPane;

    @FXML
    private GridPane root;

    @FXML
    private Rectangle rectangle;

    ConfigurationManager configManager = new ConfigurationManager();
    private final int TAB_MARGIN = 19;
    private final float HEIGHT_MULTIPLICATOR = 0.6f;
    private final float WIDTH_MULTIPLICATOR = 0.8f;


    public void initialize() {
        File file = new File("images/KalendriumLogo.png");
        if (configManager.isDarkThemeEnabled()) {
            root.getStylesheets().add("https://raw.githubusercontent.com/antoniopelusi/JavaFX-Dark-Theme/main/style.css");
        }
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
        IcsParser icsParser = new IcsParser();
        List<Cours> listCoursEnzo = icsParser.parseICSFile("schedules/users/enzo.ics");
        List<Cours> coursesOnTargetDate = getCoursesOnTargetDate(listCoursEnzo, 20, Calendar.MARCH, 2024);
        for (Cours cours : coursesOnTargetDate) {
            System.out.println("Matiere: " + cours.getMatiere());
            System.out.println("Salle: " + cours.getSalle());
            System.out.println("DateStart:" + cours.getDateStart().getTime());
            System.out.println("DateEnd:" + cours.getDateEnd().get(Calendar.HOUR_OF_DAY) + "h" + cours.getDateEnd().get(Calendar.MINUTE));
        }
    }

    public static List<Cours> getCoursesOnTargetDate(List<Cours> listCoursEnzo, int targetDay, int targetMonth, int targetYear) {
        List<Cours> coursesOnTargetDate = new ArrayList<>();
        for (Cours cours : listCoursEnzo) {
            if (cours.getDateStart().get(Calendar.DAY_OF_MONTH) == targetDay &&
                    cours.getDateStart().get(Calendar.MONTH) == targetMonth &&
                    cours.getDateStart().get(Calendar.YEAR) == targetYear) {
                coursesOnTargetDate.add(cours);
            }
        }
        return coursesOnTargetDate;
    }

    public void addEvent() {

    }

    public void openSettings() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("settings.fxml"));
        Parent settingsRoot = fxmlLoader.load();
        Pane currentRoot = (Pane) root.getScene().getRoot();
        currentRoot.getChildren().setAll(settingsRoot);
    }

    // CTRL + T to switch theme
    private void switchTheme() {
        configManager.switchTheme(root);
    }
}
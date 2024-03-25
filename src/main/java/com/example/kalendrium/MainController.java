package com.example.kalendrium;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;

import java.util.*;

import java.io.File;
import java.io.IOException;

public class MainController {
    @FXML
    private ImageView logo;

    @FXML
    private TabPane tabPane;

    @FXML
    private GridPane root;

    @FXML
    public GridPane mainGridPane;

    ConfigurationManager configManager = new ConfigurationManager();
    private final int TAB_MARGIN = 19;
    private final int NUMBER_OF_ROWS = 24;
    private double rowHeight = 28;
    private double columnWidth = 640/5;


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
            double newRectangleWidth = newValue.doubleValue();
            mainGridPane.setPrefWidth(newRectangleWidth);
            columnWidth = root.getWidth() / 5;
        });

        // Height tracker
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newRectangleHeight = newValue.doubleValue();
            mainGridPane.setPrefHeight(newRectangleHeight);
            rowHeight = root.getHeight() / NUMBER_OF_ROWS;
        });

        // Commands tracker
        Platform.runLater(() -> {
            root.getScene().setOnKeyPressed(e -> {
                if (e.isControlDown() && e.getCode() == KeyCode.T) {
                    switchTheme();
                }
            });
        });
        this.drawSchedule();
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

    public void drawSchedule() {
        mainGridPane.getChildren().clear();
        IcsParser parser = new IcsParser();
        List<Cours> courses = parser.parseICSFile("schedules/users/enzo.ics");
        Calendar startDate = Calendar.getInstance();
        startDate.set(2024, Calendar.MARCH, 18);

        for (int j = 0; j < NUMBER_OF_ROWS; j++) {
            RowConstraints rowConstraints = new RowConstraints(rowHeight);
            mainGridPane.getRowConstraints().add(rowConstraints);
        }

        for (int k = 0; k < 5; k++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth(20);
            mainGridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < 5; i++) {
            List<Cours> coursesOnTargetDate = getCoursesOnTargetDate(courses, startDate.get(Calendar.DAY_OF_MONTH),
                    startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR));
            coursesOnTargetDate.sort(Comparator.comparing(c -> c.getDateStart().getTime()));

            Label label = new Label();
            label.setAlignment(Pos.CENTER);
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            switch (i) {
                case 0 -> label.setText("Monday " + startDate.get(Calendar.DAY_OF_MONTH));
                case 1 -> label.setText("Tuesday " + startDate.get(Calendar.DAY_OF_MONTH));
                case 2 -> label.setText("Wednesday " + startDate.get(Calendar.DAY_OF_MONTH));
                case 3 -> label.setText("Thursday " + startDate.get(Calendar.DAY_OF_MONTH));
                case 4 -> label.setText("Friday " + startDate.get(Calendar.DAY_OF_MONTH));
            }
            mainGridPane.add(label, i, 0);

            for (Cours cours : coursesOnTargetDate) {
                int startHour = cours.getDateStart().get(Calendar.HOUR_OF_DAY);
                int startMinute = cours.getDateStart().get(Calendar.MINUTE);
                int endHour = cours.getDateEnd().get(Calendar.HOUR_OF_DAY);
                int endMinute = cours.getDateEnd().get(Calendar.MINUTE);
                int startRowIndex = ((startHour - 8) * 2) + (startMinute / 30) + 1;
                int endRowIndex = ((endHour - 8) * 2) + (endMinute / 30) + 1;
                int span = endRowIndex - startRowIndex;

                EventBox eventBox = new EventBox();
                eventBox.setEventDetails(cours.getDateStart(), cours.getDateEnd(), cours.getMatiere(),
                        cours.getEnseignant(), cours.getTd(), cours.getPromotion(), cours.getSalle(),
                        cours.getMemo(), cours.getType(), cours.getSummary());

                eventBox.boite.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> rowHeight * span, root.heightProperty()));
                eventBox.boite.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> columnWidth, root.widthProperty()));
                mainGridPane.add(eventBox, i, startRowIndex, 1, span);
            }
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
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
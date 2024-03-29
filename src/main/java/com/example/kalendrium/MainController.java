package com.example.kalendrium;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
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
    private int NUMBER_OF_COLUMNS = 5;
    private double rowHeight = (double) (480 / NUMBER_OF_ROWS);
    private double columnWidth = (double) 640 / NUMBER_OF_COLUMNS;
    private final Calendar startDate = Calendar.getInstance();

    public void initialize() {
        File file = new File("images/KalendriumLogo.png");
        if (configManager.isDarkThemeEnabled()) {
            root.getStylesheets().add("https://raw.githubusercontent.com/antoniopelusi/JavaFX-Dark-Theme/main/style.css");
        }
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        tabPane.setTabMaxHeight(40);
        tabPane.setTabMinHeight(40);

        for (int j = 0; j < NUMBER_OF_ROWS; j++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight((double) 100 / NUMBER_OF_ROWS);
            mainGridPane.getRowConstraints().add(rowConstraints);
        }

        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth((double) 100 / NUMBER_OF_COLUMNS);
            columnConstraints.setHalignment(HPos.CENTER);
            mainGridPane.getColumnConstraints().add(columnConstraints);
        }

        // Width tracker
        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newTabWidth = newValue.doubleValue() / 3 - TAB_MARGIN;
            tabPane.setTabMinWidth(newTabWidth);
            tabPane.setTabMaxWidth(newTabWidth);
            double newRectangleWidth = newValue.doubleValue();
            mainGridPane.setPrefWidth(newRectangleWidth);
            columnWidth = root.getWidth() / NUMBER_OF_COLUMNS;
        });

        // Height tracker
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newRectangleHeight = newValue.doubleValue();
            mainGridPane.setPrefHeight(newRectangleHeight);
            rowHeight = mainGridPane.getHeight() / NUMBER_OF_ROWS;
            mainGridPane.setMinHeight(0);
        });

        // Commands tracker
        Platform.runLater(() -> {
            root.getScene().setOnKeyPressed(e -> {
                if (e.isControlDown() && e.getCode() == KeyCode.T) {
                    switchTheme();
                }
            });
            root.getScene().setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.KP_LEFT || e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.L ) {
                    System.out.println("LEFT");
                    updateDate(-12);
                }
                if (e.getCode() == KeyCode.KP_RIGHT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.R ) {
                    System.out.println("RIGHT");
                    updateDate(2);
                }
            });
        });
        startDate.set(2024, Calendar.MARCH, 18);
        this.drawSchedule();
    }

    public void updateDate(int amount) {
        startDate.add(Calendar.DAY_OF_MONTH, amount);
        this.drawSchedule();
    }

    public void drawSchedule() {
        mainGridPane.getChildren().clear();
        IcsParser parser = new IcsParser();
        List<Cours> courses = parser.parseICSFile("schedules/users/enzo.ics");

        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            List<Cours> coursesOnTargetDate = getCoursesOnTargetDate(courses, startDate.get(Calendar.DAY_OF_MONTH),
                    startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR));
            coursesOnTargetDate.sort(Comparator.comparing(c -> c.getDateStart().getTime()));

            HBox container = new HBox();
            container.setAlignment(Pos.CENTER);
            container.setSpacing(5);

            Label label = new Label();
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            switch (i) {
                case 0 -> label.setText("Monday " + startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1));
                case 1 -> label.setText("Tuesday " + startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1));
                case 2 -> label.setText("Wednesday " + startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1));
                case 3 -> label.setText("Thursday " + startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1));
                case 4 -> label.setText("Friday " + startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1));
                case 5 -> label.setText("Saturday " + startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1));
                case 6 -> label.setText("Sunday " + startDate.get(Calendar.DAY_OF_MONTH) + "/" + (startDate.get(Calendar.MONTH) + 1));
            }
            container.getChildren().add(label);

            if (i == 0) {
                Label leftArrow = new Label("←");
                leftArrow.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                leftArrow.setOnMouseClicked((MouseEvent event) -> {
                    updateDate(-12);
                });
                container.getChildren().add(0, leftArrow);
            }
            if (i == NUMBER_OF_COLUMNS - 1) {
                Label rightArrow = new Label("→");
                rightArrow.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                rightArrow.setOnMouseClicked((MouseEvent event) -> {
                    updateDate(2);
                });
                container.getChildren().add(rightArrow);
            }

            mainGridPane.add(container, i, 0);

            for (Cours cours : coursesOnTargetDate) {
                if (coursesOnTargetDate.size() == 0) {
                    continue;
                }

                int startHour = cours.getDateStart().get(Calendar.HOUR_OF_DAY);
                int startMinute = cours.getDateStart().get(Calendar.MINUTE);
                int endHour = cours.getDateEnd().get(Calendar.HOUR_OF_DAY);
                int endMinute = cours.getDateEnd().get(Calendar.MINUTE);
                int startRowIndex = ((startHour - 8) * 2) + (startMinute / 30) + 1;
                if (startRowIndex < 1) {
                    startRowIndex = 1;
                }
                int endRowIndex = ((endHour - 8) * 2) + (endMinute / 30) + 1;
                int span = endRowIndex - startRowIndex;

                EventBox eventBox = new EventBox();
                eventBox.setEventDetails(cours.getDateStart(), cours.getDateEnd(), cours.getMatiere(),
                        cours.getEnseignant(), cours.getTd(), cours.getPromotion(), cours.getSalle(),
                        cours.getMemo(), cours.getType(), cours.getSummary());

                eventBox.boite.setMinHeight(0);
                eventBox.boite.prefHeightProperty().bind(Bindings.multiply(Bindings.divide(mainGridPane.heightProperty(), NUMBER_OF_ROWS - 1), span));
                eventBox.boite.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> columnWidth, root.widthProperty()));
                mainGridPane.add(eventBox, i, startRowIndex);
            }
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    public static List<Cours> getCoursesOnTargetDate(List<Cours> courses, int targetDay, int targetMonth, int targetYear) {
        List<Cours> coursesOnTargetDate = new ArrayList<>();
        for (Cours cours : courses) {
            if (cours.getDateStart().get(Calendar.DAY_OF_MONTH) == targetDay &&
                    cours.getDateStart().get(Calendar.MONTH) == targetMonth &&
                    cours.getDateStart().get(Calendar.YEAR) == targetYear) {
                coursesOnTargetDate.add(cours);
            }
        }
        return coursesOnTargetDate;
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
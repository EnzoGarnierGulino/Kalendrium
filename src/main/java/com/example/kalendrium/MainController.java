package com.example.kalendrium;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

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
            mainGridPane.setPrefWidth(newRectangleWidth);
        });
        // Height tracker
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newRectangleHeight = newValue.doubleValue() * HEIGHT_MULTIPLICATOR;
            mainGridPane.setPrefHeight(newRectangleHeight);
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

    public void addEvent() {

    }

    public void drawSchedule() {
        IcsParser parser = new IcsParser();
        List<Cours> listCoursEnzo = parser.parseICSFile("schedules/users/enzo.ics");
        Calendar startDate = Calendar.getInstance();
        startDate.set(2024, Calendar.MARCH, 18); // Start from Monday

        for (int i = 0; i < 5; i++) { // Loop for each day from Monday to Friday
            List<Cours> coursesOnTargetDate = getCoursesOnTargetDate(listCoursEnzo, startDate.get(Calendar.DAY_OF_MONTH),
                    startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR));

            // Sort the courses by start time
            coursesOnTargetDate.sort(Comparator.comparing(c -> c.getDateStart().getTime()));

            // Keep track of the occupied rows to handle course overlap
            boolean[] occupiedRows = new boolean[24 * 2]; // Assuming 30-minute intervals, from 8:00 to 20:00

            // Iterate through courses on the target date and add them to the grid pane
            for (Cours cours : coursesOnTargetDate) {
                // Calculate the start and end time of the course
                int startHour = cours.getDateStart().get(Calendar.HOUR_OF_DAY);
                int startMinute = cours.getDateStart().get(Calendar.MINUTE);
                int endHour = cours.getDateEnd().get(Calendar.HOUR_OF_DAY);
                int endMinute = cours.getDateEnd().get(Calendar.MINUTE);

                // Calculate row index for the current course
                int startRowIndex = ((startHour - 8) * 2) + (startMinute / 30);
                int endRowIndex = ((endHour - 8) * 2) + (endMinute / 30);

                // Find an available row for the course
                int rowIndex = findAvailableRow(startRowIndex, endRowIndex, occupiedRows);

                // Mark the rows occupied by this course
                for (int j = startRowIndex; j < endRowIndex; j++) {
                    occupiedRows[j] = true;
                }

                // Calculate column index for the current day
                int column = i;

                // Create and set details for the event box
                EventBox eventBox = new EventBox();
                eventBox.setEventDetails(cours.getDateStart(), cours.getDateEnd(), cours.getMatiere(),
                        cours.getEnseignant(), cours.getTd(), cours.getPromotion(), cours.getSalle(),
                        cours.getMemo(), cours.getType(), cours.getSummary());

                // Add the event box to the grid pane
                mainGridPane.add(eventBox, column, rowIndex);
            }

            // Move to the next day
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
    }

    private int findAvailableRow(int startRow, int endRow, boolean[] occupiedRows) {
        int rowIndex = startRow;
        while (rowIndex < occupiedRows.length && occupiedRows[rowIndex]) {
            rowIndex++;
        }
        return rowIndex;
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
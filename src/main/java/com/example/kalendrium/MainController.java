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
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;

import java.text.SimpleDateFormat;
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
        for (int i = 0; i < 24; i++) { // Assuming 18 rows, adjust as needed
            RowConstraints rowConstraints = new RowConstraints(30); // Adjust height as needed
            mainGridPane.getRowConstraints().add(rowConstraints);
        }

        for (int i = 0; i < 5; i++) { // Loop for each day from Monday to Friday
            List<Cours> coursesOnTargetDate = getCoursesOnTargetDate(listCoursEnzo, startDate.get(Calendar.DAY_OF_MONTH),
                    startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR));

            // Sort the courses by start time
            coursesOnTargetDate.sort(Comparator.comparing(c -> c.getDateStart().getTime()));

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

                System.out.println("Course: " + cours.getMatiere() + " - " + startRowIndex + " - " + endRowIndex);

                int span = endRowIndex - startRowIndex;

                // Create and set details for the event box
                EventBox eventBox = new EventBox();
                eventBox.setEventDetails(cours.getDateStart(), cours.getDateEnd(), cours.getMatiere(),
                        cours.getEnseignant(), cours.getTd(), cours.getPromotion(), cours.getSalle(),
                        cours.getMemo(), cours.getType(), cours.getSummary());

                // Add the event box to the grid pane
                System.out.println("Adding event box to grid pane");
                System.out.println("Row index: " + startRowIndex + " - Span: " + span);
                mainGridPane.add(eventBox, i, startRowIndex, 1, span);
            }

            // Move to the next day
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

    private static List<List<List<Cours>>> getLists(List<Cours> coursByDate) {
        List<List<List<Cours>>> LALIST = new ArrayList<>();
        Calendar latestCours = coursByDate.get(0).getDateStart();
        boolean coursNotPlaced = true;
        for (Cours cours : coursByDate) {
            if (latestCours.before(cours.getDateStart()) || latestCours.equals(cours.getDateStart())) {
                LALIST.add(new ArrayList<>());
            }
            List<List<Cours>> latestBlock = LALIST.get(LALIST.size()-1);
            if (latestBlock.isEmpty()) {
                latestBlock.add(new ArrayList<>());
                latestBlock.get(0).add(cours);
                if (cours.getDateEnd().after(latestCours)) {
                    latestCours = cours.getDateEnd();
                }
            } else {
                for (List<Cours> list : latestBlock) {
                    if (list.get(list.size()-1).getDateEnd().before(cours.getDateStart()) || list.get(list.size()-1).getDateEnd().equals(cours.getDateStart())) {
                        list.add(cours);
                        if (cours.getDateEnd().after(latestCours)) {
                            latestCours = cours.getDateEnd();
                        }
                        coursNotPlaced = false;
                        break;
                    }
                }
                if (coursNotPlaced) {
                    latestBlock.add(new ArrayList<>());
                    latestBlock.get(latestBlock.size()-1).add(cours);
                    if (cours.getDateEnd().after(latestCours)) {
                        latestCours = cours.getDateEnd();
                    }
                }
                coursNotPlaced = true;
            }
        }
        return LALIST;
    }
}
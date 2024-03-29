package com.example.kalendrium;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TabPane;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import org.controlsfx.control.CheckComboBox;


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

    @FXML
    public CheckComboBox<String> matieres;
    @FXML
    public CheckComboBox<String> salles;
    @FXML
    public CheckComboBox<String> promotions;
    @FXML
    public CheckComboBox<String> types;


    ConfigurationManager configManager = new ConfigurationManager();
    private final int TAB_MARGIN = 19;
    private final float HEIGHT_MULTIPLICATOR = 0.6f;
    private final float WIDTH_MULTIPLICATOR = 0.8f;


    public void initialize() {

        matieres.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                drawSchedule();
            }
        });
        promotions.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                drawSchedule();
            }
        });
        types.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                drawSchedule();
            }
        });
        salles.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                drawSchedule();
            }
        });
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
        mainGridPane.getChildren().clear();

        DateTime dtStart = CreateEvent.createDateTime(2024, 3, 18, 11, 30);
        DateTime dtEnd = CreateEvent.createDateTime(2024, 3, 18, 13, 0);
        VEvent event = CreateEvent.createEvent(dtStart, dtEnd, "Bonjour", "C'est un test", "la bas");
        CreateEvent.addNoneOverlapingEvent("schedules/users/enzo.ics", event);


        IcsParser parser = new IcsParser();
        List<Cours> listCoursEnzo = parser.parseICSFile("schedules/users/enzo.ics");

        List<Cours> memoire = listCoursEnzo;

        List<List<String>> uniqueProperties = CoursUtils.getUniqueCoursProperties(listCoursEnzo);
        setCheckComboBoxFiltres(uniqueProperties);
        List<String> selectedMatieres = this.matieres.getCheckModel().getCheckedItems();
        List<String> selectedSalles = this.salles.getCheckModel().getCheckedItems();
        List<String> selectedPromotions = this.promotions.getCheckModel().getCheckedItems();
        List<String> selectedTypes = this.types.getCheckModel().getCheckedItems();

        listCoursEnzo = CoursFilter.filterCours(listCoursEnzo, selectedMatieres, selectedSalles, selectedPromotions, selectedTypes);
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

//                System.out.println("Course: " + cours.getMatiere() + " - " + startRowIndex + " - " + endRowIndex);

                int span = endRowIndex - startRowIndex;

                // Create and set details for the event box
                EventBox eventBox = new EventBox();
                eventBox.setEventDetails(cours.getDateStart(), cours.getDateEnd(), cours.getMatiere(),
                        cours.getEnseignant(), cours.getTd(), cours.getPromotion(), cours.getSalle(),
                        cours.getMemo(), cours.getType(), cours.getSummary());

                // Add the event box to the grid pane
//                System.out.println("Adding event box to grid pane");
//                System.out.println("Row index: " + startRowIndex + " - Span: " + span);
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

    public void setCheckComboBoxFiltres(List<List<String>> uniqueProperties) {
        if (matieres.getItems().isEmpty()) {
            matieres.getItems().addAll(uniqueProperties.get(0));
        }
        if (salles.getItems().isEmpty()) {
            salles.getItems().addAll(uniqueProperties.get(1));
        }
        if (promotions.getItems().isEmpty()) {
            promotions.getItems().addAll(uniqueProperties.get(2));
        }
        if (types.getItems().isEmpty()) {
            types.getItems().addAll(uniqueProperties.get(3));
        }
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
package com.example.kalendrium;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import javafx.scene.layout.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

import java.io.File;

public class MainController {
    @FXML
    private ImageView logo;
    @FXML
    private GridPane root;
    @FXML
    public GridPane mainGridPane;
    @FXML
    public ComboBox<String> mode;
    @FXML
    public ComboBox<String> courseComboBox;
    @FXML
    private ComboBox<String> filterComboBox;
    @FXML
    public CheckComboBox<String> matieres;
    @FXML
    public CheckComboBox<String> salles;
    @FXML
    public CheckComboBox<String> promotions;
    @FXML
    public CheckComboBox<String> types;
    @FXML
    public Button addEventButton;
    @FXML
    public Button bookButton;
    @FXML
    public Button logoutButton;
    ConfigurationManager configManager = new ConfigurationManager();
    private final int NUMBER_OF_ROWS = 25;
    private int NUMBER_OF_COLUMNS = 5;
    private int numberOfDaysToGoAfter = 2;
    private int numberOfDaysToGoBefore = -12;
    private double columnWidth = (double) 640 / NUMBER_OF_COLUMNS;
    private final Calendar startDate = Calendar.getInstance();
    private String schedulePath = "";

    public void initialize() {
        String id = null;
        try (FileReader reader = new FileReader("db/currentUser.json")) {
            JSONParser parser = new JSONParser();
            JSONObject currentUserJson = (JSONObject) parser.parse(reader);
            id = (String) currentUserJson.get("currentUserId");
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        if (!Objects.equals(id, null)) {
            if (!configManager.isAdmin(id)) {
                bookButton.setVisible(false);
            }
        }

        // TODO: Bug here, the comboboxes aren't actualized when the user changes the mode or the course
        matieres.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends String> c) -> {
            resetToMonday();
        });
        promotions.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends String> c) -> {
            resetToMonday();
        });
        salles.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends String> c) -> {
            resetToMonday();
        });
        types.getCheckModel().getCheckedItems().addListener((ListChangeListener.Change<? extends String> c) -> {
            resetToMonday();
        });

        File file = new File("images/KalendriumLogo.png");
        Image image = new Image(file.toURI().toString());
        logo.setImage(image);
        filterComboBox.setValue("Week");

        for (int j = 0; j < NUMBER_OF_ROWS; j++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setPercentHeight((double) 100 / NUMBER_OF_ROWS);
            mainGridPane.getRowConstraints().add(rowConstraints);
        }
        initializeColumns();

        // Width tracker
        root.widthProperty().addListener((observable, oldValue, newValue) -> {
            double newRectangleWidth = newValue.doubleValue();
            mainGridPane.setPrefWidth(newRectangleWidth);
            columnWidth = root.getWidth() / NUMBER_OF_COLUMNS;
        });

        // Height tracker
        root.heightProperty().addListener((observable, oldValue, newValue) -> {
            double newRectangleHeight = newValue.doubleValue();
            mainGridPane.setPrefHeight(newRectangleHeight);
            mainGridPane.setMinHeight(0);
        });

        // Mode tracker
        mode.valueProperty().addListener((observable, oldValue, newValue) -> {
            matieres.getCheckModel().clearChecks();
            salles.getCheckModel().clearChecks();
            promotions.getCheckModel().clearChecks();
            types.getCheckModel().clearChecks();
            populateComboBox(newValue);
            schedulePath = "schedules/" + newValue.toLowerCase() + "/" + courseComboBox.getValue() + ".ics";
            filterComboBox.setValue("Week");
            resetToMonday();
        });

        mode.setValue("Users");
        populateComboBox(mode.getValue());

        // Schedule tracker
        courseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                schedulePath = "schedules/" + mode.getValue().toLowerCase() +
                        "/" + newValue + ".ics";
                filterComboBox.setValue("Week");
                resetToMonday();
            }
        });

        // Filter tracker
        filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case "Day" -> {
                    NUMBER_OF_COLUMNS = 1;
                    numberOfDaysToGoAfter = 0;
                    numberOfDaysToGoBefore = -2;
                    startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    initializeColumns();
                }
                case "Week" -> {
                    NUMBER_OF_COLUMNS = 5;
                    numberOfDaysToGoAfter = 2;
                    numberOfDaysToGoBefore = -12;
                    startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                    initializeColumns();
                }
                case "Month" -> {
                    NUMBER_OF_COLUMNS = startDate.get(Calendar.DAY_OF_MONTH);
                    numberOfDaysToGoAfter = 0;
                    numberOfDaysToGoBefore = startDate.get(Calendar.DAY_OF_MONTH) * -1 - startDate.get(Calendar.DAY_OF_MONTH);
                    startDate.set(Calendar.DAY_OF_MONTH, 1);
                    initializeColumns();
                }
            }

            columnWidth = root.getWidth() / NUMBER_OF_COLUMNS;
            this.drawSchedule(schedulePath);
        });

        // Buttons tracker
        addEventButton.setOnAction(event -> {
            openAddEventWindow(mainGridPane);
        });
        bookButton.setOnAction(event -> {
            openBookEventWindow(mainGridPane);
        });
        logoutButton.setOnAction(event -> {
            logout();
        });

        // Commands tracker
        Platform.runLater(() -> {
            root.getScene().setOnKeyPressed(e -> {
                // → to go to the next day / week / month
                if (e.getCode() == KeyCode.KP_LEFT || e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.L ) {
                    updateDate(numberOfDaysToGoBefore);
                }
                // ← to go to the previous day / week / month
                if (e.getCode() == KeyCode.KP_RIGHT || e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.R ) {
                    updateDate(numberOfDaysToGoAfter);
                }
                // Backspace to logout
                if (e.getCode() == KeyCode.BACK_SPACE) {
                    logout();
                }
                // CTRL + T to switch theme
                if (e.isControlDown() && e.getCode() == KeyCode.T) {
                    switchTheme();
                }
            });
        });

        schedulePath = "schedules/" + mode.getValue().toLowerCase()
                + "/" + courseComboBox.getValue() + ".ics";
        startDate.set(2024, Calendar.APRIL, 1);
        this.drawSchedule(schedulePath);
    }

    public void resetToMonday() {
        startDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        drawSchedule(schedulePath);
    }

    public void logout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = fxmlLoader.load();
            Stage mainStage = new Stage();
            mainStage.setTitle("Kalendrium");
            mainStage.setScene(new Scene(root));
            mainStage.setResizable(false);
            mainStage.setWidth(400);
            mainStage.setHeight(340);
            mainStage.getIcons().add(new Image("file:images/K.png"));
            mainStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeColumns() {
        mainGridPane.getColumnConstraints().clear();
        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setPercentWidth((double) 100 / NUMBER_OF_COLUMNS);
            columnConstraints.setHalignment(HPos.CENTER);
            mainGridPane.getColumnConstraints().add(columnConstraints);
        }
    }

    public void populateComboBox(String id) {
        File folder = new File("schedules/" + id.toLowerCase());
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                ObservableList<String> filenames = FXCollections.observableArrayList();
                for (File file : files) {
                    if (file.isFile()) {
                        String name = file.getName();
                        name = Character.toUpperCase(name.charAt(0)) + name.substring(1);
                        filenames.add(name.replace(".ics", ""));
                    }
                }
                courseComboBox.setItems(filenames);
                if (!filenames.isEmpty()) {
                    courseComboBox.setValue(filenames.get(0));
                }
            }
        }
    }

    public void updateDate(int amount) {
        startDate.add(Calendar.DAY_OF_MONTH, amount);
        this.drawSchedule(schedulePath);
    }

    public void drawSchedule(String schedulePath) {
        mainGridPane.getChildren().clear();
        IcsParser parser = new IcsParser();
        List<Cours> courses = parser.parseICSFile(schedulePath);

        List<List<String>> uniqueProperties = CoursUtils.getUniqueCoursProperties(courses);
        setCheckComboBoxFiltres(uniqueProperties);
        List<String> selectedMatieres = this.matieres.getCheckModel().getCheckedItems();
        List<String> selectedSalles = this.salles.getCheckModel().getCheckedItems();
        List<String> selectedPromotions = this.promotions.getCheckModel().getCheckedItems();
        List<String> selectedTypes = this.types.getCheckModel().getCheckedItems();

        courses = CoursFilter.filterCours(courses, selectedMatieres, selectedSalles, selectedPromotions, selectedTypes);

        for (int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            List<Cours> coursesOnTargetDate = getCoursesOnTargetDate(courses, startDate.get(Calendar.DAY_OF_MONTH),
                    startDate.get(Calendar.MONTH), startDate.get(Calendar.YEAR));
            coursesOnTargetDate.sort(Comparator.comparing(c -> c.getDateStart().getTime()));

            HBox container = new HBox();
            container.setAlignment(Pos.CENTER);

            Label label = new Label();
            label.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            SimpleDateFormat dateFormat = new SimpleDateFormat("E dd/MM", Locale.ENGLISH);
            Calendar dayCalendar = (Calendar) startDate.clone();
            label.setText(dateFormat.format(dayCalendar.getTime()));
            container.getChildren().add(label);

            if (i == 0) {
                Label leftArrow = new Label("←");
                leftArrow.setStyle("-fx-font-weight: bold; -fx-font-size: 40px;");
                leftArrow.setOnMouseClicked((MouseEvent event) -> {
                    updateDate(numberOfDaysToGoBefore);
                });
                container.getChildren().add(0, leftArrow);
            }
            if (i == NUMBER_OF_COLUMNS - 1) {
                Label rightArrow = new Label("→");
                rightArrow.setStyle("-fx-font-weight: bold; -fx-font-size: 40px;");
                rightArrow.setOnMouseClicked((MouseEvent event) -> {
                    updateDate(numberOfDaysToGoAfter);
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

                // TODO: Send email with teacher's name
                eventBox.setOnMouseClicked(event -> {
                    System.out.println("Teacher's name: " + cours.getEnseignant());
                });

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

    public void openAddEventWindow(GridPane mainGridPane) {
        mainGridPane.getChildren().clear();

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        mainGridPane.addRow(0, dateLabel, datePicker);

        Label startHourLabel = new Label("Start Hour:");
        ComboBox<String> startHourComboBox = new ComboBox<>();
        populateHourComboBox(startHourComboBox);
        mainGridPane.addRow(2, startHourLabel, startHourComboBox);

        Label endHourLabel = new Label("End Hour:");
        ComboBox<String> endHourComboBox = new ComboBox<>();
        populateHourComboBox(endHourComboBox);
        mainGridPane.addRow(4, endHourLabel, endHourComboBox);

        Label colorLabel = new Label("Color:");
        ColorPicker colorPicker = new ColorPicker();
        mainGridPane.addRow(6, colorLabel, colorPicker);

        Label nameLabel = new Label("Name:");
        TextArea nameField = new TextArea();
        mainGridPane.addRow(8, nameLabel, nameField);

        Label descriptionLabel = new Label("Description:");
        TextArea descriptionArea = new TextArea();
        mainGridPane.addRow(10, descriptionLabel, descriptionArea);

        Button addButton = new Button("Add");
        addButton.setOnAction(event -> {
            // TODO: Create event with the given information
            System.out.println("Date: " + datePicker.getValue());
            System.out.println("Start Hour: " + startHourComboBox.getValue());
            System.out.println("End Hour: " + endHourComboBox.getValue());
            System.out.println("Color: " + colorPicker.getValue());
            System.out.println("Name: " + nameField.getText());
            System.out.println("Description: " + descriptionArea.getText());
            mainGridPane.getChildren().clear();
            startDate.set(2024, Calendar.APRIL, 1);
            drawSchedule(schedulePath);
        });
        mainGridPane.addRow(12, addButton);
    }

    public void openBookEventWindow(GridPane mainGridPane) {
        mainGridPane.getChildren().clear();

        Label dateLabel = new Label("Date:");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        mainGridPane.addRow(0, dateLabel, datePicker);

        Label startHourLabel = new Label("Start Hour:");
        ComboBox<String> startHourComboBox = new ComboBox<>();
        populateHourComboBox(startHourComboBox);
        mainGridPane.addRow(2, startHourLabel, startHourComboBox);

        Label endHourLabel = new Label("End Hour:");
        ComboBox<String> endHourComboBox = new ComboBox<>();
        populateHourComboBox(endHourComboBox);
        mainGridPane.addRow(4, endHourLabel, endHourComboBox);

        // TODO: Get rooms from schedule ?
        Label roomLabel = new Label("Room:");
        TextField room = new TextField();
        mainGridPane.addRow(6, roomLabel, room);

        Button addButton = new Button("Book");
        addButton.setOnAction(event -> {
            // TODO: Book a room with the given information
            System.out.println("Date: " + datePicker.getValue());
            System.out.println("Start Hour: " + startHourComboBox.getValue() + " End Hour: " + endHourComboBox.getValue());
            System.out.println("Room: " + room.getText());
            mainGridPane.getChildren().clear();
            startDate.set(2024, Calendar.APRIL, 1);
            drawSchedule(schedulePath);
        });
        mainGridPane.addRow(10, addButton);
    }

    private void populateHourComboBox(ComboBox<String> comboBox) {
        List<String> hours = new ArrayList<>();
        for (int hour = 8; hour <= 20; hour++) {
            hours.add(String.valueOf(hour));
            hours.add(hour + ":30");
        }
        ObservableList<String> hourOptions = FXCollections.observableArrayList(hours);
        comboBox.setItems(hourOptions);
        comboBox.getSelectionModel().select(0);
    }

    // CTRL + T to switch theme
    private void switchTheme() {
        configManager.switchTheme(root);
    }

    private static List<List<List<Cours>>> getLists(List<Cours> coursByDate) {
        List<List<List<Cours>>> finalList = new ArrayList<>();
        Calendar latestCours = coursByDate.get(0).getDateStart();
        boolean coursNotPlaced = true;
        for (Cours cours : coursByDate) {
            if (latestCours.before(cours.getDateStart()) || latestCours.equals(cours.getDateStart())) {
                finalList.add(new ArrayList<>());
            }
            List<List<Cours>> latestBlock = finalList.get(finalList.size()-1);
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
        return finalList;
    }
}
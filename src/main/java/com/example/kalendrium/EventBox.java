package com.example.kalendrium;

import java.io.IOException;
import java.util.Calendar;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

public class EventBox extends Pane {

    @FXML
    private Pane boite;
    @FXML
    private Label heure;
    @FXML
    private Label matiere;
    @FXML
    private Label enseignant;
    @FXML
    private Label td;
    @FXML
    private Label salle;
    @FXML
    private Label type;
    @FXML
    private Label memo;

    private Calendar dateStart;
    private Calendar dateEnd;
    private String promotion;
    private String summary;

    public EventBox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cours-component.fxml"));
        fxmlLoader.setController(this);
        try {
            this.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setEventDetails(Calendar dateStart, Calendar dateEnd, String matiere, String enseignant, String td, String promotion, String salle, String memo, String type, String summary) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.promotion = promotion;
        this.summary = summary;

        long dureeMinutes = (dateEnd.getTimeInMillis() - dateStart.getTimeInMillis()) / (1000 * 60);

        this.heure.setText(dateStart.get(Calendar.HOUR_OF_DAY) + "h" + String.format("%02d", dateStart.get(Calendar.MINUTE)) + " - " + dateEnd.get(Calendar.HOUR_OF_DAY) + "h" + String.format("%02d", dateEnd.get(Calendar.MINUTE)));
        this.matiere.setText(matiere);
        this.enseignant.setText(enseignant);
        this.td.setText(td);
        this.salle.setText(salle);
        this.type.setText(type);
        this.memo.setText(memo);

        boite.setPrefHeight(dureeMinutes);
        this.heure.setPrefHeight(2);
        if (type.equals("Evaluation")) {
            boite.setStyle("-fx-background-color: #c53e3e; -fx-background-radius: 3px;\n" +
                    "-fx-border-color: black; -fx-border-width: 2px;");
        }
    }
}

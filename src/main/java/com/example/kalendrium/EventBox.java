package com.example.kalendrium;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.ResourceBundle;

import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

public class EventBox extends Pane implements Initializable {

    @FXML
    protected VBox boite;
    @FXML
    protected Label heure;
    @FXML
    protected Label matiere;
    @FXML
    protected Label enseignant;
    @FXML
    protected Label td;
    @FXML
    protected Label salle;
    @FXML
    protected Label type;
    @FXML
    protected Label memo;

    protected Calendar dateStart;
    protected Calendar dateEnd;
    protected String promotion;
    protected String summary;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        BooleanBinding boolHeure = boite.heightProperty().greaterThan(20);
        heure.visibleProperty().bind(boolHeure);
        BooleanBinding boolMatiere = boite.heightProperty().greaterThan(40);
        matiere.visibleProperty().bind(boolMatiere);
        BooleanBinding boolEnseignant = boite.heightProperty().greaterThan(60);
        enseignant.visibleProperty().bind(boolEnseignant);
        BooleanBinding boolTd = boite.heightProperty().greaterThan(80);
        td.visibleProperty().bind(boolTd);
        BooleanBinding boolSalle = boite.heightProperty().greaterThan(100);
        salle.visibleProperty().bind(boolSalle);
        BooleanBinding boolType = boite.heightProperty().greaterThan(120);
        type.visibleProperty().bind(boolType);
        BooleanBinding boolMemo = boite.heightProperty().greaterThan(140);
        memo.visibleProperty().bind(boolMemo);
    }

    public EventBox() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("cours-component.fxml"));
        fxmlLoader.setController(this);
        try {
            this.getChildren().add(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.td.setWrapText(true);
        this.memo.setWrapText(true);
        this.heure.setWrapText(true);
        this.matiere.setWrapText(true);
        this.enseignant.setWrapText(true);
        this.salle.setWrapText(true);
        this.type.setWrapText(true);
    }

    public void setEventDetails(Calendar dateStart, Calendar dateEnd, String matiere, String enseignant, String td, String promotion, String salle, String memo, String type, String summary, String color) {
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.promotion = promotion;
        this.summary = summary;
        this.heure.setText(dateStart.get(Calendar.HOUR_OF_DAY) + "h" + String.format("%02d", dateStart.get(Calendar.MINUTE)) + " - " + dateEnd.get(Calendar.HOUR_OF_DAY) + "h" + String.format("%02d", dateEnd.get(Calendar.MINUTE)));
        this.matiere.setText(matiere);
        this.enseignant.setText(enseignant);
        this.td.setText(td);
        this.salle.setText(salle);
        this.type.setText(type);
        this.memo.setText(memo);

        if (matiere != null && !matiere.isEmpty()) {
            if (matiere.equals("Reservation de salles")) {
                boite.setStyle("-fx-background-color: #258111; -fx-background-radius: 3px;\n" +
                        "-fx-border-color: black; -fx-border-width: 2px;");
            }
        }
        if (type != null && !type.isEmpty()) {
            if (type.equals("Evaluation")) {
                boite.setStyle("-fx-background-color: #c53e3e; -fx-background-radius: 3px;\n" +
                        "-fx-border-color: black; -fx-border-width: 2px;");
            }
        }
        if (color != null && !color.isEmpty()) {
            boite.setStyle("-fx-background-color: #" + color + "; -fx-background-radius: 3px;\n" +
                    "-fx-border-color: black; -fx-border-width: 2px;");
        }
    }
}

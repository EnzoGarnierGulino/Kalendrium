package com.example.kalendrium;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        IcsParser parser = new IcsParser();
        List<Cours> listCoursShana = parser.parseICSFile("schedules/users/shana.ics");
        List<Cours> listCoursEnzo = parser.parseICSFile("schedules/users/enzo.ics");
        List<Cours> listCoursSabri = parser.parseICSFile("schedules/users/sabri.ics");

        
        List<Cours> coursesOnTargetDate = new ArrayList<>();
        
        coursesOnTargetDate = getCoursesOnTargetDate(listCoursEnzo, 5, Calendar.MARCH, 2024);
        
        for (Cours cours : coursesOnTargetDate) {

            System.out.println("Matiere: " + cours.getMatiere());
            System.out.println("Salle: " + cours.getSalle());
            System.out.println("DateStart:" + cours.getDateStart().getTime());
            System.out.println("DateEnd:" + cours.getDateEnd().get(Calendar.HOUR_OF_DAY) + "h" + cours.getDateEnd().get(Calendar.MINUTE));
        }
        
        launch();
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
}
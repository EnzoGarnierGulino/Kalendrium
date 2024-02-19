package com.example.kalendrium;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
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
        
        launch();
    }
}
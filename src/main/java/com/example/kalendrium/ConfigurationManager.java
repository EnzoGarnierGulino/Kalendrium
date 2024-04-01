package com.example.kalendrium;

import javafx.scene.layout.Pane;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationManager {
    private static final String DB_FILE_PATH = "db/db.json";
    private static final JSONParser parser = new JSONParser();
    private JSONObject jsonObject;
    private JSONArray users;
    private boolean isDarkTheme;

    public ConfigurationManager() {
        loadConfig();
    }

    private void loadConfig() {
        try (FileReader reader = new FileReader(DB_FILE_PATH)) {
            Object obj = parser.parse(reader);
            if (obj instanceof JSONArray) {
                users = (JSONArray) obj;
            } else {
                System.err.println("Invalid JSON format: Root object is not a JSONArray");
                users = new JSONArray();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            users = new JSONArray();
        }
    }

    public void switchTheme(Pane root) {
        if (isDarkTheme) {
            root.getStylesheets().add("https://raw.githubusercontent.com/antoniopelusi/JavaFX-Dark-Theme/main/style.css");
        } else {
            root.getStylesheets().remove("https://raw.githubusercontent.com/antoniopelusi/JavaFX-Dark-Theme/main/style.css");
        }
        isDarkTheme = !isDarkTheme;
    }

    public boolean isAdmin(String id) {
        for (Object userObject : users) {
            JSONObject user = (JSONObject) userObject;
            String userId = user.get("id").toString();
            if (userId != null && userId.equals(id)) {
                return (boolean) user.get("isAdmin");
            }
        }
        return false;
    }
}
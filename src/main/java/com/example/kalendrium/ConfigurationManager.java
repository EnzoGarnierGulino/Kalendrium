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
    private JSONArray users;;

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

    public void switchTheme(Pane root, String userId) {
        JSONObject user = null;
        for (Object obj : users) {
            JSONObject jsonObject = (JSONObject) obj;
            String id = (String) jsonObject.get("id");
            if (id.equals(userId)) {
                user = jsonObject;
                break;
            }
        }
        if (user != null) {
            boolean darkTheme = (boolean) user.get("darkTheme");
            user.put("darkTheme", !darkTheme);
            saveChangesToFile();
            if (!darkTheme) {
                root.getStylesheets().add("https://raw.githubusercontent.com/antoniopelusi/JavaFX-Dark-Theme/main/style.css");
            } else {
                root.getStylesheets().remove("https://raw.githubusercontent.com/antoniopelusi/JavaFX-Dark-Theme/main/style.css");
            }
        } else {
            System.out.println("User not found in the database.");
        }
    }

    public boolean isDarkModeEnabled(String userId) {
        for (Object obj : users) {
            JSONObject user = (JSONObject) obj;
            String id = (String) user.get("id");
            if (id.equals(userId)) {
                return (boolean) user.get("darkTheme");
            }
        }
        System.out.println("User not found in the database.");
        return false;
    }

    public void setDarkTheme(Pane root) {
        root.getStylesheets().add("https://raw.githubusercontent.com/antoniopelusi/JavaFX-Dark-Theme/main/style.css");
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

    private void saveChangesToFile() {
        try (FileWriter file = new FileWriter(DB_FILE_PATH)) {
            file.write(users.toJSONString());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
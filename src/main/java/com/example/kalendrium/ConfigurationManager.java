package com.example.kalendrium;

import javafx.scene.layout.Pane;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ConfigurationManager {
    private static final String DB_FILE_PATH = "db/db.json";
    private static final JSONParser parser = new JSONParser();
    private JSONObject jsonObject;

    public ConfigurationManager() {
        loadConfig();
    }

    private void loadConfig() {
        try (FileReader reader = new FileReader(DB_FILE_PATH)) {
            jsonObject = (JSONObject) parser.parse(reader);
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
            jsonObject = new JSONObject();
        }
    }

    public boolean isDarkThemeEnabled() {
        return jsonObject.containsKey("darkTheme") && (boolean) jsonObject.get("darkTheme");
    }

    public void setDarkTheme(boolean enabled) {
        jsonObject.put("darkTheme", enabled);
        saveConfig();
    }

    private void saveConfig() {
        try (FileWriter writer = new FileWriter(DB_FILE_PATH)) {
            writer.write(jsonObject.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchTheme(Pane root) {
        boolean currentTheme = this.isDarkThemeEnabled();
        boolean newTheme = !currentTheme;
        this.setDarkTheme(newTheme);
        if (newTheme) {
            root.getStylesheets().add("https://raw.githubusercontent.com/antoniopelusi/JavaFX-Dark-Theme/main/style.css");
        } else {
            root.getStylesheets().clear();
        }
    }
}
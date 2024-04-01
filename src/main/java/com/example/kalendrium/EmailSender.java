package com.example.kalendrium;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailSender {
    private static final String DOMAIN = "@univ-avignon.fr";

    public static void sendEmail(String name) {
        if (name == null || name.isEmpty()) {
            System.out.println("Invalid name. Cannot generate email address.");
            return;
        }
        String toEmail = generateEmailAddress(name);
        if (toEmail.isEmpty()) {
            System.out.println("Invalid name format. Cannot generate email address.");
            return;
        }

        String mailto = "mailto:" + toEmail + DOMAIN;
        try {
            URI uri = new URI(mailto);
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.MAIL)) {
                desktop.mail(uri);
            }
        } catch (URISyntaxException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static String generateEmailAddress(String name) {
        String firstNamePattern = "\\b[A-Z][a-z](?:[- ]?[a-z])*\\b";
        String lastNamePattern = "\\b[A-Z](?:[- ]?[A-Z])*\\b";

        String toEmail = "";
        toEmail += extractAndFormatNamePart(name, firstNamePattern);
        toEmail += "." + extractAndFormatNamePart(name, lastNamePattern);

        return toEmail;
    }

    private static String extractAndFormatNamePart(String name, String patternString) {
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(name);
        if (matcher.find()) {
            return matcher.group().replaceAll(" ", "-").toLowerCase();
        }
        return "";
    }
}

package com.example.kalendrium;

import java.util.ArrayList;
import java.util.List;

public class CoursUtils {
    public static List<List<String>> getUniqueCoursProperties(List<Cours> coursList) {
        List<List<String>> uniqueProperties = new ArrayList<>();
        List<String> matieres = new ArrayList<>();
        List<String> promotions = new ArrayList<>();
        List<String> salles = new ArrayList<>();
        List<String> types = new ArrayList<>();

        for (Cours cours : coursList) {
            if (cours.getMatiere() != null && !matieres.contains(cours.getMatiere())) {
                matieres.add(cours.getMatiere());
            }
            if (cours.getPromotion() != null) {
                String[] promotionNames = cours.getPromotion().split(",");
                for (String promotionName : promotionNames) {
                    promotionName = promotionName.trim(); // remove leading/trailing whitespace
                    if (!promotions.contains(promotionName)) {
                        promotions.add(promotionName);
                    }
                }
            }
            if (cours.getTd() != null) {
                String[] promotionNames = cours.getTd().split(",");
                for (String promotionName : promotionNames) {
                    promotionName = promotionName.trim(); // remove leading/trailing whitespace
                    if (!promotions.contains(promotionName)) {
                        promotions.add(promotionName);
                    }
                }
            }
            if (cours.getSalle() != null) {
                String[] salleNames = cours.getSalle().split(",");
                for (String salle : salleNames) {
                    salle = salle.trim();
                    if (!salles.contains(salle)) {
                        salles.add(salle);
                    }
                }
            }
            if (cours.getType() != null && !types.contains(cours.getType())) {
                types.add(cours.getType());
            }
        }

        uniqueProperties.add(matieres);
        uniqueProperties.add(promotions);
        uniqueProperties.add(salles);
        uniqueProperties.add(types);

        return uniqueProperties;
    }
}

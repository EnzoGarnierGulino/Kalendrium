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
            if (cours.getPromotion() != null && !promotions.contains(cours.getPromotion())) {
                promotions.add(cours.getPromotion());
            }
            if (cours.getSalle() != null && !salles.contains(cours.getSalle())) {
                salles.add(cours.getSalle());
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

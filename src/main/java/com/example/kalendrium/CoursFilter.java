package com.example.kalendrium;

import java.util.ArrayList;
import java.util.List;

public class CoursFilter {
    public static List<Cours> filterCours(List<Cours> coursList, List<String> matieres, List<String> promotions, List<String> salles, List<String> types) {
        List<Cours> filteredCoursList = new ArrayList<>();

        for (Cours cours : coursList) {
            boolean isMatiereValid = matieres == null || matieres.isEmpty() || matieres.contains(cours.getMatiere());
            boolean isPromotionValid = promotions == null || promotions.isEmpty() || promotions.contains(cours.getPromotion());
            boolean isSalleValid = salles == null || salles.isEmpty() || salles.contains(cours.getSalle());
            boolean isTypeValid = types == null || types.isEmpty() || types.contains(cours.getType());

            if (isMatiereValid && isPromotionValid && isSalleValid && isTypeValid) {
                filteredCoursList.add(cours);
            }
        }

        return filteredCoursList;
    }
}
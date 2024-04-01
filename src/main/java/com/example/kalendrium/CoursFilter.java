package com.example.kalendrium;

import java.util.ArrayList;
import java.util.List;

public class CoursFilter {
    public static List<Cours> filterCours(List<Cours> coursList, List<String> matieres, List<String> promotions, List<String> salles, List<String> types) {
        List<Cours> filteredCoursList = new ArrayList<>();

        for (Cours cours : coursList) {
            boolean isMatiereValid = matieres == null || matieres.isEmpty() || matieres.contains(cours.getMatiere());
            boolean isPromotionValid = false;
            if (promotions != null && !promotions.isEmpty()) {
                if (cours.getPromotion() != null) {
                    String[] promotionNames = cours.getPromotion().split(",");
                    for (String promotionName : promotionNames) {
                        promotionName = promotionName.trim();
                        if (promotions.contains(promotionName)) {
                            isPromotionValid = true;
                            break;
                        }
                    }
                }
                if (cours.getTd() != null) {
                    String[] promotionNames = cours.getTd().split(",");
                    for (String promotionName : promotionNames) {
                        promotionName = promotionName.trim();
                        if (promotions.contains(promotionName)) {
                            isPromotionValid = true;
                            break;
                        }
                    }
                }
            } else {
               isPromotionValid = true;
            }
            boolean isSalleValid = false;
            if (salles != null && !salles.isEmpty()) {
               if (cours.getSalle() != null) {
                   String[] salleNames = cours.getSalle().split(",");
                   for (String salleName : salleNames) {
                       salleName = salleName.trim();
                       if (salles.contains(salleName)) {
                           isSalleValid = true;
                           break;
                       }
                   }
               }
            } else {
               isSalleValid = true;
            }
            boolean isTypeValid = types == null || types.isEmpty() || types.contains(cours.getType());

            if (isMatiereValid && isPromotionValid && isSalleValid && isTypeValid) {
                filteredCoursList.add(cours);
            }
        }

        return filteredCoursList;
    }
}
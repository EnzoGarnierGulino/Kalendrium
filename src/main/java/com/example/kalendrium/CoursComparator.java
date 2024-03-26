package com.example.kalendrium;

import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

public class CoursComparator implements Comparator<Cours> {
    public int compare(Cours c1, Cours c2) {
        Calendar cal1 = c1.getDateStart();
        Calendar cal2 = c2.getDateStart();
        return cal1.compareTo(cal2);
    }

    public int getNombreCoursEnCours(Calendar moment, List<Cours> coursList) {
        int compteur = 0;
        for (Cours cours : coursList) {
            Calendar dateDebut = cours.getDateStart();
            Calendar dateFin = cours.getDateEnd();
            if (moment.after(dateDebut)) {
                System.out.println(moment.before(dateFin));
                System.out.println("NIQUE JAVA");
            }
            if (moment.after(dateDebut) && moment.before(dateFin)) {
                compteur++;
            }
        }
        return compteur;
    }
}
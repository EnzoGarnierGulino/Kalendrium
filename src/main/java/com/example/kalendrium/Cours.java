package com.example.kalendrium;

import java.util.Calendar;

public class Cours {
    
    private Calendar dateStart;
    private Calendar dateEnd;
    private String matiere;
    private String enseignant;
    private String td;
    private String promotion;
    private String salle;
    private String memo;
    private String type;
    private String summary;

    public Cours() {
        this.dateStart = null;
        this.dateEnd = null;
        this.matiere = null;
        this.enseignant = null;
        this.td = null;
        this.promotion = null;
        this.salle = null;
        this.memo = null;
        this.type = null;
    }

    public Calendar getDateStart() {
        return dateStart;
    }

    public Calendar getDateEnd() {
        return dateEnd;
    }

    public String getMatiere() {
        return matiere;
    }

    public String getEnseignant() {
        return enseignant;
    }

    public String getTd() {
        return td;
    }

    public String getPromotion() {
        return promotion;
    }

    public String getSalle() {
        return salle;
    }

    public String getMemo() {
        return memo;
    }

    public String getType() {
        return type;
    }

    public String getSummary() {
        return summary;
    }

    public void setDateStart(Calendar dateStart) {
        this.dateStart = dateStart;
    }
    
    public void setDateEnd(Calendar dateEnd) {
        this.dateEnd = dateEnd;
    }
    
    public void setMatiere(String matiere) {
        this.matiere = matiere;
    }
    
    public void setEnseignant(String enseignant) {
        this.enseignant = enseignant;
    }
    
    public void setTd(String td) {
        this.td = td;
    }
    
    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }
    
    public void setSalle(String salle) {
        this.salle = salle;
    }
    
    public void setMemo(String memo) {
        this.memo = memo;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Cours{" +
            "dateStart=" + dateStart +
            ", dateEnd=" + dateEnd +
            ", matiere='" + matiere + '\'' +
            ", enseignant='" + enseignant + '\'' +
            ", td='" + td + '\'' +
            ", promotion='" + promotion + '\'' +
            ", salle='" + salle + '\'' +
            ", memo='" + memo + '\'' +
            ", type='" + type + '\'' +
            ", summary='" + summary + '\'' +
            '}';
    }
}

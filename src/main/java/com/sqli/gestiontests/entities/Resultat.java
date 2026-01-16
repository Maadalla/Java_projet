package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "resultats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resultat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "test_id", unique = true, nullable = false)
    private Test test;

    @Column(nullable = false)
    private Integer score; // Nombre de bonnes réponses

    @Column(name = "total_questions", nullable = false)
    private Integer totalQuestions;

    @Column(nullable = false)
    private Double note; // Note sur 20

    @Column(nullable = false)
    private Double pourcentage; // Pourcentage de réussite

    @Column(name = "date_calcul", nullable = false)
    private LocalDateTime dateCalcul;

    @Column(name = "email_envoye", nullable = false)
    private Boolean emailEnvoye = false;

    @PrePersist
    protected void onCreate() {
        dateCalcul = LocalDateTime.now();
    }

    public void calculerNote() {
        if (totalQuestions > 0) {
            this.pourcentage = (score * 100.0) / totalQuestions;
            this.note = (score * 20.0) / totalQuestions;
        } else {
            this.pourcentage = 0.0;
            this.note = 0.0;
        }
    }

    public String getMention() {
        if (note >= 16)
            return "Excellent";
        if (note >= 14)
            return "Très Bien";
        if (note >= 12)
            return "Bien";
        if (note >= 10)
            return "Assez Bien";
        return "Insuffisant";
    }

    public String getDateFormatee() {
        if (dateCalcul == null)
            return "";
        return java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").format(dateCalcul);
    }

    public String getPourcentageFormatted() {
        return String.format("%.0f%%", pourcentage != null ? pourcentage : 0.0);
    }
}

package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "reponses_candidats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReponseCandidat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "test_id", nullable = false)
    private Test test;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne
    @JoinColumn(name = "reponse_id")
    private Reponse reponse;

    // Pour les questions à choix multiples, on stocke les IDs des réponses
    // sélectionnées
    @Column(name = "reponses_multiples", length = 500)
    private String reponsesMultiples; // Format: "1,3,5" (IDs séparés par virgule)

    @Column(nullable = false)
    private Boolean correcte = false;

    public void setReponsesMultiplesList(java.util.List<Long> ids) {
        this.reponsesMultiples = ids.stream()
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(","));
    }

    public java.util.List<Long> getReponsesMultiplesList() {
        if (reponsesMultiples == null || reponsesMultiples.isEmpty()) {
            return new java.util.ArrayList<>();
        }
        return java.util.Arrays.stream(reponsesMultiples.split(","))
                .map(Long::parseLong)
                .collect(java.util.stream.Collectors.toList());
    }
}

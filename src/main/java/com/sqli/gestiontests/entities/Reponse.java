package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Entity
@Table(name = "reponses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reponse implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le texte de la réponse est obligatoire")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;

    @Column(nullable = false)
    private Boolean correcte = false;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    @lombok.ToString.Exclude
    private Question question;
}

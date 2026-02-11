package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Configuration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cle", nullable = false, unique = true, length = 100)
    private String cle;

    @Column(name = "valeur", nullable = false, length = 500)
    private String valeur;

    @Column(length = 500)
    private String description;

    // Clés de configuration standard
    public static final String DUREE_EXAMEN = "duree.examen";
    public static final String NOMBRE_QUESTIONS_PAR_THEME = "nombre.questions.par.theme";
    public static final String TEMPS_PAR_QUESTION = "temps.par.question";
    public static final String VALIDATION_ADMIN_REQUISE = "validation.admin.requise";
}

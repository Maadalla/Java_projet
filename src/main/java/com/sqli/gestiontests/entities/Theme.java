package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "themes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Theme implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du thème est obligatoire")
    @Column(nullable = false, unique = true, length = 100)
    private String nom;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean actif = true;

    @OneToMany(mappedBy = "theme", cascade = CascadeType.ALL)
    @lombok.ToString.Exclude
    private List<Question> questions = new ArrayList<>();

    public int getNombreQuestions() {
        return questions.size();
    }
}

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
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le texte de la question est obligatoire")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String texte;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeQuestion type;

    @ManyToOne
    @JoinColumn(name = "theme_id", nullable = false)
    private Theme theme;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Reponse> reponses = new ArrayList<>();

    @Column(nullable = false)
    private Boolean actif = true;

    public enum TypeQuestion {
        CHOIX_UNIQUE("Choix unique"),
        CHOIX_MULTIPLE("Choix multiple");

        private final String label;

        TypeQuestion(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    public List<Reponse> getReponsesCorrectes() {
        List<Reponse> correctes = new ArrayList<>();
        for (Reponse r : reponses) {
            if (r.getCorrecte()) {
                correctes.add(r);
            }
        }
        return correctes;
    }
}

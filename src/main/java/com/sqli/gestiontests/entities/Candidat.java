package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "candidats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidat implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false, length = 100)
    private String prenom;

    @NotBlank(message = "L'école est obligatoire")
    @Column(nullable = false, length = 200)
    private String ecole;

    @Column(length = 100)
    private String filiere;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @NotBlank(message = "Le GSM est obligatoire")
    @Pattern(regexp = "^[0-9]{10}$", message = "Le GSM doit contenir 10 chiffres")
    @Column(nullable = false, length = 20)
    private String gsm;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutInscription statut = StatutInscription.EN_ATTENTE;

    @ManyToOne
    @JoinColumn(name = "creneau_id")
    private Creneau creneau;

    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Test> tests = new ArrayList<>();

    public enum StatutInscription {
        EN_ATTENTE,
        VALIDEE,
        REJETEE
    }

    @PrePersist
    protected void onCreate() {
        dateInscription = LocalDateTime.now();
    }
}

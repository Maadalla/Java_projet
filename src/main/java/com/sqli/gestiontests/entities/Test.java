package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Test implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "candidat_id", nullable = false)
    private Candidat candidat;

    @ManyToOne
    @JoinColumn(name = "creneau_id", nullable = false)
    private Creneau creneau;

    @Column(name = "date_debut")
    private LocalDateTime dateDebut;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "duree_configuree", nullable = false)
    private Integer dureeConfiguree; // en minutes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutTest statut = StatutTest.EN_COURS;

    @OneToMany(mappedBy = "test", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<ReponseCandidat> reponsesCandidat = new ArrayList<>();

    @OneToOne(mappedBy = "test", cascade = CascadeType.ALL)
    private Resultat resultat;

    public enum StatutTest {
        EN_COURS,
        TERMINE,
        ABANDONNE
    }

    @PrePersist
    protected void onCreate() {
        dateDebut = LocalDateTime.now();
    }

    public void terminer() {
        this.dateFin = LocalDateTime.now();
        this.statut = StatutTest.TERMINE;
    }
}

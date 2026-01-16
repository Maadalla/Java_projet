package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "creneaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Creneau implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La date est obligatoire")
    @Column(nullable = false)
    private LocalDate date;

    @NotNull(message = "L'heure de début est obligatoire")
    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @NotNull(message = "L'heure de fin est obligatoire")
    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Min(value = 30, message = "La durée minimale est 30 minutes")
    @Column(name = "duree_examen", nullable = false)
    private Integer dureeExamen; // en minutes

    @Min(value = 1, message = "La capacité doit être au moins 1")
    @Column(name = "capacite_max", nullable = false)
    private Integer capaciteMax = 50;

    @Column(nullable = false)
    private Boolean actif = true;

    @OneToMany(mappedBy = "creneau", fetch = FetchType.EAGER)
    private List<Candidat> candidats = new ArrayList<>();

    public boolean isDisponible() {
        return actif && candidats.size() < capaciteMax;
    }

    public int getPlacesRestantes() {
        return capaciteMax - candidats.size();
    }
}

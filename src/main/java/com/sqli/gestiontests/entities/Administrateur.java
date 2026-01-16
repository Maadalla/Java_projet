package com.sqli.gestiontests.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;

@Entity
@Table(name = "administrateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrateur implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le login est obligatoire")
    @Column(nullable = false, unique = true, length = 50)
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String password; // Stocké hashé avec BCrypt

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Email invalide")
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false)
    private Boolean actif = true;
}

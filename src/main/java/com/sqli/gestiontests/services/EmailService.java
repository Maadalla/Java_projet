package com.sqli.gestiontests.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.Serializable;
import java.util.Properties;

public class EmailService implements Serializable {

    // Config SMTP (A configurer dans config.properties idéalement)
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_FROM = "ton.email@gmail.com"; // Remplacer par vrai email
    private static final String EMAIL_PASSWORD = "ton-mot-de-passe-app"; // Remplacer par App Password

    public void envoyerEmailAvecCertificat(String destinataire, String sujet, String corps, byte[] pdfContent,
            String nomFichierPdf) {
        System.out.println("--- Tentative d'envoi d'email à " + destinataire + " ---");

        // 1. Configuration des propriétés
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        // 2. Création de la session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            // 3. Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);

            // 4. Contenu Multipart (Texte + PDF)
            Multipart multipart = new MimeMultipart();

            // Partie Texte
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(corps);
            multipart.addBodyPart(messageBodyPart);

            // Partie Pièce jointe (PDF)
            if (pdfContent != null && pdfContent.length > 0) {
                MimeBodyPart pdfBodyPart = new MimeBodyPart();
                DataSource source = new ByteArrayDataSource(pdfContent, "application/pdf");
                pdfBodyPart.setDataHandler(new DataHandler(source));
                pdfBodyPart.setFileName(nomFichierPdf);
                multipart.addBodyPart(pdfBodyPart);
            }

            message.setContent(multipart);

            // 5. Envoi
            Transport.send(message);

            System.out.println("✅ Email envoyé avec succès à " + destinataire);

        } catch (Exception e) {
            // MOCK MODE : Si l'envoi échoue (ex: auth, connexion), on log pour le dev
            System.err.println("⚠️ Echec envoi email réel. Passage en mode SIMULATION/MOCK.");
            System.err.println("Erreur: " + e.getMessage());

            System.out.println("-------------------------------------------------------------");
            System.out.println("✉️ [EMAIL MOCK] Destinataire: " + destinataire);
            System.out.println("✉️ [EMAIL MOCK] Sujet: " + sujet);
            System.out.println("✉️ [EMAIL MOCK] Corps: \n" + corps);
            if (pdfContent != null) {
                System.out.println("📎 [EMAIL MOCK] Pièce jointe incluse: " + nomFichierPdf + " (" + pdfContent.length
                        + " bytes)");
            }
            System.out.println("-------------------------------------------------------------");
            // On ne relance pas l'exception pour ne pas bloquer l'appli
        }
    }
}

package com.sqli.gestiontests.utils;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailSender {

    private static Properties emailConfig;

    static {
        emailConfig = new Properties();
        try (InputStream input = EmailSender.class.getClassLoader()
                .getResourceAsStream("application.properties")) {
            if (input != null) {
                emailConfig.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Envoie un email
     */
    public static void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        String host = emailConfig.getProperty("mail.smtp.host", "smtp.gmail.com");
        String port = emailConfig.getProperty("mail.smtp.port", "587");
        String username = emailConfig.getProperty("mail.username", "");
        String password = emailConfig.getProperty("mail.password", "");
        String from = emailConfig.getProperty("mail.from", "noreply@gestiontests.com");

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", emailConfig.getProperty("mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", emailConfig.getProperty("mail.smtp.starttls.enable", "true"));

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");

        Transport.send(message);
    }

    /**
     * Génère l'email de confirmation d'inscription
     */
    public static String generateInscriptionEmail(String nom, String prenom, String code,
            String creneau, boolean validationRequise) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2>Confirmation d'inscription</h2>");
        html.append("<p>Bonjour ").append(prenom).append(" ").append(nom).append(",</p>");

        if (validationRequise) {
            html.append("<p>Votre inscription a bien été enregistrée et est en cours de validation.</p>");
            html.append(
                    "<p>Vous recevrez un email de confirmation avec votre code de session une fois votre inscription validée.</p>");
        } else {
            html.append("<p>Votre inscription a bien été confirmée!</p>");
            html.append("<div style='background-color: #f0f0f0; padding: 15px; margin: 20px 0;'>");
            html.append("<h3>Votre code de session:</h3>");
            html.append("<p style='font-size: 24px; font-weight: bold; color: #007bff;'>").append(code).append("</p>");
            html.append("<p><strong>Créneau:</strong> ").append(creneau).append("</p>");
            html.append("</div>");
            html.append("<p>Conservez précieusement ce code pour accéder à votre test.</p>");
        }

        html.append("<p>Cordialement,<br>L'équipe de Gestion de Tests en Ligne</p>");
        html.append("</body></html>");
        return html.toString();
    }

    /**
     * Génère l'email de résultat
     */
    public static String generateResultatEmail(String nom, String prenom, double note,
            int score, int total, String mention) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'></head><body style='font-family: Arial, sans-serif;'>");
        html.append("<h2>Résultats de votre test</h2>");
        html.append("<p>Bonjour ").append(prenom).append(" ").append(nom).append(",</p>");
        html.append("<p>Voici les résultats de votre test:</p>");
        html.append("<div style='background-color: #f0f0f0; padding: 15px; margin: 20px 0;'>");
        html.append("<p><strong>Score:</strong> ").append(score).append(" / ").append(total).append("</p>");
        html.append("<p><strong>Note:</strong> ").append(String.format("%.2f", note)).append(" / 20</p>");
        html.append("<p><strong>Mention:</strong> ").append(mention).append("</p>");
        html.append("</div>");
        html.append("<p>Cordialement,<br>L'équipe de Gestion de Tests en Ligne</p>");
        html.append("</body></html>");
        return html.toString();
    }
}

package com.sqli.gestiontests.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.sqli.gestiontests.entities.Resultat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class CertificateService {

    public byte[] genererCertificat(Resultat resultat) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Cadre
        Rectangle rect = new Rectangle(577, 403, 18, 15); // A4 Landscape approx
        rect.enableBorderSide(1);
        rect.enableBorderSide(2);
        rect.enableBorderSide(4);
        rect.enableBorderSide(8);
        rect.setBorderColor(BaseColor.DARK_GRAY);
        rect.setBorderWidth(5);
        // document.add(rect); // Simplification: pas de cadre complexe sans positions
        // absolues

        // Style
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 36, BaseColor.DARK_GRAY);
        Font subTitleFont = FontFactory.getFont(FontFactory.HELVETICA, 18, BaseColor.GRAY);
        Font nameFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 30, new BaseColor(44, 62, 80)); // Navy Blue
        Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 16, BaseColor.BLACK);
        Font scoreFont = FontFactory.getFont(FontFactory.COURIER_BOLD, 20, new BaseColor(39, 174, 96)); // Green

        // Header
        Paragraph title = new Paragraph("CERTIFICAT DE RÉUSSITE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingBefore(40);
        document.add(title);

        Paragraph subtitle = new Paragraph("ISGA Management", subTitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(40);
        document.add(subtitle);

        // Separator
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(new BaseColor(200, 200, 200));
        document.add(new Chunk(ls));

        // Body
        Paragraph body1 = new Paragraph("\nCe certificat est décerné à", subTitleFont);
        body1.setAlignment(Element.ALIGN_CENTER);
        body1.setSpacingBefore(30);
        document.add(body1);

        String nomComplet = resultat.getTest().getCandidat().getPrenom().toUpperCase() + " "
                + resultat.getTest().getCandidat().getNom().toUpperCase();
        Paragraph name = new Paragraph(nomComplet, nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingAfter(20);
        document.add(name);

        Paragraph body2 = new Paragraph("Pour avoir réussi l'examen technique avec succès.", textFont);
        body2.setAlignment(Element.ALIGN_CENTER);
        document.add(body2);

        // Score Section
        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(60);
        table.setSpacingBefore(40);
        table.setSpacingAfter(40);

        PdfPCell cell1 = new PdfPCell(new Phrase("Note Obtenue :", textFont));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell1.setPaddingRight(20);

        PdfPCell cell2 = new PdfPCell(new Phrase(String.format("%.1f / 20", resultat.getNote()), scoreFont));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);

        table.addCell(cell1);
        table.addCell(cell2);
        document.add(table);

        // Footer
        Paragraph date = new Paragraph("Fait le : " + resultat.getDateFormatee(), textFont);
        date.setAlignment(Element.ALIGN_RIGHT);
        date.setIndentationRight(50);
        document.add(date);

        document.close();
        return out.toByteArray();
    }
}

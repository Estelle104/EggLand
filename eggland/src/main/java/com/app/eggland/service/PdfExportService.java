package com.app.eggland.service;

import com.app.eggland.model.DetailVente;
import com.app.eggland.model.Livraison;
import com.app.eggland.model.PaiementSalaire;
import com.app.eggland.repository.DetailVenteRepository;
import com.app.eggland.repository.LivraisonRepository;
import com.app.eggland.repository.PaiementSalaireRepository;
import com.app.eggland.repository.VenteRepository;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfExportService {

    private final LivraisonRepository livraisonRepository;
    private final VenteRepository venteRepository;
    private final DetailVenteRepository detailVenteRepository;
    private final PaiementSalaireRepository paiementSalaireRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final float[] BON_LIVRAISON_COLS = {1.5f, 3f, 2f, 2f, 2f, 2f};
    private static final float[] PAIEMENT_SALAIRE_COLS = {2f, 2f, 2f, 2f, 2f};

    public byte[] generateBonLivraisonPdf(LocalDate date) throws IOException {
        List<Livraison> livraisons = livraisonRepository.findByDateLivraison(date);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());
        document.setMargins(36, 36, 36, 36);

        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        document.add(new Paragraph("BON DE LIVRAISON - " + date.format(DATE_FMT))
                .setFont(fontBold).setFontSize(18).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        Table table = new Table(UnitValue.createPercentArray(BON_LIVRAISON_COLS)).useAllAvailableWidth();
        addHeaderRow(table, fontBold, "N° Livraison", "Client", "Date Livraison", "Adresse", "Produit", "Quantité");
        table.setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));

        BigDecimal totalQuantite = BigDecimal.ZERO;
        BigDecimal totalMontant = BigDecimal.ZERO;

        for (Livraison l : livraisons) {
            List<DetailVente> details = detailVenteRepository.findByVenteId(l.getVente().getId());
            String clientName = l.getClient().getNom();
            if (l.getClient().getPrenom() != null && !l.getClient().getPrenom().isBlank()) {
                clientName += " " + l.getClient().getPrenom();
            }
            String adresse = l.getAdresseLivraison() != null ? l.getAdresseLivraison() : "";

            if (details.isEmpty()) {
                table.addCell(createCell(l.getId().toString(), font, TextAlignment.CENTER));
                table.addCell(createCell(clientName, font, TextAlignment.LEFT));
                table.addCell(createCell(l.getDateLivraison().format(DATE_FMT), font, TextAlignment.CENTER));
                table.addCell(createCell(adresse, font, TextAlignment.LEFT));
                table.addCell(createCell("-", font, TextAlignment.CENTER));
                table.addCell(createCell("-", font, TextAlignment.CENTER));
            } else {
                for (DetailVente d : details) {
                    BigDecimal qty = d.getQuantite() != null ? d.getQuantite() : BigDecimal.ZERO;
                    BigDecimal prix = d.getPrixUnitaire() != null ? d.getPrixUnitaire() : BigDecimal.ZERO;
                    totalQuantite = totalQuantite.add(qty);
                    totalMontant = totalMontant.add(qty.multiply(prix));

                    table.addCell(createCell(l.getId().toString(), font, TextAlignment.CENTER));
                    table.addCell(createCell(clientName, font, TextAlignment.LEFT));
                    table.addCell(createCell(l.getDateLivraison().format(DATE_FMT), font, TextAlignment.CENTER));
                    table.addCell(createCell(adresse, font, TextAlignment.LEFT));
                    table.addCell(createCell(d.getProduit() != null ? d.getProduit().getCode() : "-", font, TextAlignment.CENTER));
                    table.addCell(createCell(qty.toString(), font, TextAlignment.CENTER));
                }
            }
        }

        document.add(table);

        document.add(new Paragraph("\nTotal Quantité: " + totalQuantite + " | Total Montant: " + totalMontant + " Ar")
                .setFont(fontBold).setFontSize(12).setTextAlignment(TextAlignment.RIGHT).setMarginTop(20));

        document.close();
        return baos.toByteArray();
    }

    public byte[] generatePaiementSalairePdf(LocalDate mois) throws IOException {
        List<PaiementSalaire> paiements = paiementSalaireRepository.findByMoisOrderByEmployeNomAsc(mois);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(36, 36, 36, 36);

        PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

        document.add(new Paragraph("ÉTAT DE PAIEMENT DES SALAIRES - " + mois.format(DateTimeFormatter.ofPattern("MM/yyyy")))
                .setFont(fontBold).setFontSize(18).setTextAlignment(TextAlignment.CENTER).setMarginBottom(20));

        Table table = new Table(UnitValue.createPercentArray(PAIEMENT_SALAIRE_COLS)).useAllAvailableWidth();
        addHeaderRow(table, fontBold, "Employé", "Mois", "Montant", "Statut", "Date Paiement");
        table.setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f));

        BigDecimal total = BigDecimal.ZERO;
        for (PaiementSalaire ps : paiements) {
            total = total.add(ps.getMontant());
            table.addCell(createCell(ps.getEmploye().getPrenom() + " " + ps.getEmploye().getNom(), font, TextAlignment.LEFT));
            table.addCell(createCell(ps.getMois().format(DATE_FMT), font, TextAlignment.CENTER));
            table.addCell(createCell(ps.getMontant() + " Ar", font, TextAlignment.RIGHT));
            table.addCell(createCell(ps.getPaye() ? "Payé" : "En attente", font, TextAlignment.CENTER));
            table.addCell(createCell(ps.getDatePaiement() != null ? ps.getDatePaiement().format(DATE_FMT) : "-", font, TextAlignment.CENTER));
        }

        document.add(table);

        document.add(new Paragraph("\nTotal: " + total + " Ar")
                .setFont(fontBold).setFontSize(12).setTextAlignment(TextAlignment.RIGHT).setMarginTop(20));

        document.close();
        return baos.toByteArray();
    }

    public byte[] generateRapportFinancePdf(LocalDate debut, LocalDate fin) throws IOException {
        // This would be implemented in FinanceService or here if needed
        return new byte[0];
    }

    private void addHeaderRow(Table table, PdfFont fontBold, String... headers) {
        for (String h : headers) {
            Cell cell = new Cell().add(new Paragraph(h).setFont(fontBold).setFontSize(10).setFontColor(ColorConstants.WHITE))
                    .setBackgroundColor(ColorConstants.BLUE).setTextAlignment(TextAlignment.CENTER)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f)).setPadding(5);
            table.addHeaderCell(cell);
        }
    }

    private Cell createCell(String text, PdfFont font, TextAlignment alignment) {
        return new Cell().add(new Paragraph(text).setFont(font).setFontSize(9))
                .setTextAlignment(alignment).setBorder(new SolidBorder(ColorConstants.GRAY, 0.5f)).setPadding(4);
    }
}
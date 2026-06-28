package com.app.eggland.service;

import com.app.eggland.model.*;
import com.app.eggland.repository.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    private final OeufProductionRepository oeufProductionRepository;
    private final MvtStockRepository mvtStockRepository;
    private final PaiementSalaireRepository paiementSalaireRepository;
    private final VenteRepository venteRepository;
    private final DetailVenteRepository detailVenteRepository;
    private final ClientRepository clientRepository;
    private final ProduitVenteRepository produitVenteRepository;
    private final StatutVenteRepository statutVenteRepository;

    public ExcelExportService(OeufProductionRepository oeufProductionRepository,
                              MvtStockRepository mvtStockRepository,
                              PaiementSalaireRepository paiementSalaireRepository,
                              VenteRepository venteRepository,
                              DetailVenteRepository detailVenteRepository,
                              ClientRepository clientRepository,
                              ProduitVenteRepository produitVenteRepository,
                              StatutVenteRepository statutVenteRepository) {
        this.oeufProductionRepository = oeufProductionRepository;
        this.mvtStockRepository = mvtStockRepository;
        this.paiementSalaireRepository = paiementSalaireRepository;
        this.venteRepository = venteRepository;
        this.detailVenteRepository = detailVenteRepository;
        this.clientRepository = clientRepository;
        this.produitVenteRepository = produitVenteRepository;
        this.statutVenteRepository = statutVenteRepository;
    }

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public byte[] exportProductionOeufs(LocalDate debut, LocalDate fin) throws IOException {
        List<OeufProduction> productions = oeufProductionRepository.findByDateBetweenOrderByDateDesc(debut, fin);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Production Oeufs");
            createHeaderRow(sheet, "Date", "Lot", "Race", "Bâtiment", "Quantité", "Statuts");

            int rowNum = 1;
            int totalQuantite = 0;
            for (OeufProduction p : productions) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(p.getDate().format(DATE_FMT));
                row.createCell(1).setCellValue(p.getLot().getId());
                row.createCell(2).setCellValue(p.getLot().getRace().getNom());
                row.createCell(3).setCellValue(p.getLot().getBatiment().getNom());
                row.createCell(4).setCellValue(p.getQuantite());
                row.createCell(5).setCellValue(p.getOeufStatuts().stream()
                        .map(s -> s.getStatut().getCode() + ":" + s.getQuantite())
                        .reduce((a, b) -> a + ", " + b).orElse(""));
                totalQuantite += p.getQuantite();
            }

            Row totalRow = sheet.createRow(rowNum);
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            totalRow.createCell(0).setCellValue("TOTAL");
            totalRow.getCell(0).setCellStyle(boldStyle);
            totalRow.createCell(4).setCellValue(totalQuantite);
            totalRow.getCell(4).setCellStyle(boldStyle);

            autoSizeColumns(sheet, 6);
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] exportMouvementStock(LocalDate debut, LocalDate fin) throws IOException {
        List<MvtStock> mouvements = mvtStockRepository.findByDateBetweenOrderByDateDesc(debut, fin);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Mouvements Stock");
            createHeaderRow(sheet, "Date", "Type", "Nourriture", "Lot", "Quantité");

            int rowNum = 1;
            for (MvtStock m : mouvements) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(m.getDate().format(DATE_FMT));
                row.createCell(1).setCellValue(m.getType().getCode());
                row.createCell(2).setCellValue(m.getNourriture().getLibelle());
                row.createCell(3).setCellValue(m.getLot() != null ? m.getLot().getId().toString() : "");
                row.createCell(4).setCellValue(m.getQuantite().doubleValue());
            }

            autoSizeColumns(sheet, 5);
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    public byte[] exportPaiementsSalaires(LocalDate mois) throws IOException {
        List<PaiementSalaire> paiements = paiementSalaireRepository.findByMoisOrderByEmployeNomAsc(mois);

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Paiements Salaires");
            createHeaderRow(sheet, "Employé", "Mois", "Montant (Ar)", "Statut", "Date Paiement");

            int rowNum = 1;
            BigDecimal total = BigDecimal.ZERO;
            CellStyle currencyStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            currencyStyle.setDataFormat(format.getFormat("#,##0.00"));

            for (PaiementSalaire ps : paiements) {
                total = total.add(ps.getMontant());
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(ps.getEmploye().getPrenom() + " " + ps.getEmploye().getNom());
                row.createCell(1).setCellValue(ps.getMois().format(DATE_FMT));
                Cell montantCell = row.createCell(2);
                montantCell.setCellValue(ps.getMontant().doubleValue());
                montantCell.setCellStyle(currencyStyle);
                row.createCell(3).setCellValue(ps.getPaye() ? "Payé" : "En attente");
                row.createCell(4).setCellValue(ps.getDatePaiement() != null ? ps.getDatePaiement().format(DATE_FMT) : "");
            }

            Row totalRow = sheet.createRow(rowNum);
            CellStyle boldStyle = workbook.createCellStyle();
            Font boldFont = workbook.createFont();
            boldFont.setBold(true);
            boldStyle.setFont(boldFont);
            boldStyle.setDataFormat(format.getFormat("#,##0.00"));
            totalRow.createCell(0).setCellValue("TOTAL");
            totalRow.getCell(0).setCellStyle(boldStyle);
            Cell totalCell = totalRow.createCell(2);
            totalCell.setCellValue(total.doubleValue());
            totalCell.setCellStyle(boldStyle);

            autoSizeColumns(sheet, 5);
            workbook.write(baos);
            return baos.toByteArray();
        }
    }

    @Transactional
    public ImportResult importListeVentes(byte[] excelData) throws IOException {
        ImportResult result = new ImportResult();
        StatutVente statutDefault = statutVenteRepository.findByCode("en_attente")
                .orElse(null);
        try (Workbook workbook = new XSSFWorkbook(new java.io.ByteArrayInputStream(excelData))) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    String clientNom = getStringCellValue(row.getCell(0));
                    String dateStr = getStringCellValue(row.getCell(1));
                    String produitCode = getStringCellValue(row.getCell(2));
                    String quantiteStr = getStringCellValue(row.getCell(3));
                    String prixStr = getStringCellValue(row.getCell(4));

                    if (clientNom.isEmpty() || dateStr.isEmpty() || produitCode.isEmpty() || quantiteStr.isEmpty() || prixStr.isEmpty()) {
                        result.addError("Ligne " + (i+1) + ": Données manquantes");
                        continue;
                    }

                    Client client = clientRepository.findByNomContainingIgnoreCase(clientNom)
                            .orElseThrow(() -> new IllegalArgumentException("Client introuvable: " + clientNom));

                    ProduitVente produit = produitVenteRepository.findByCode(produitCode)
                            .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + produitCode));

                    LocalDate date = LocalDate.parse(dateStr, DATE_FMT);
                    BigDecimal quantite = new BigDecimal(quantiteStr);
                    BigDecimal prix = new BigDecimal(prixStr);
                    BigDecimal total = quantite.multiply(prix);

                    Vente vente = Vente.builder()
                            .client(client)
                            .date(date)
                            .total(total)
                            .statut(statutDefault)
                            .build();
                    vente = venteRepository.save(vente);

                    DetailVente detail = DetailVente.builder()
                            .vente(vente)
                            .client(client)
                            .produit(produit)
                            .quantite(quantite)
                            .prixUnitaire(prix)
                            .build();
                    detailVenteRepository.save(detail);

                    result.addSuccess("Ligne " + (i+1) + ": Vente " + vente.getId() + " créée - " + clientNom + " - " + produitCode + " - " + quantite + " x " + prix);
                } catch (Exception e) {
                    result.addError("Ligne " + (i+1) + ": " + e.getMessage());
                }
            }
        }
        return result;
    }

    private String getStringCellValue(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FMT);
                }
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> cell.getStringCellValue().trim();
            default -> "";
        };
    }

    private void createHeaderRow(Sheet sheet, String... headers) {
        Row headerRow = sheet.createRow(0);
        CellStyle headerStyle = sheet.getWorkbook().createCellStyle();
        Font headerFont = sheet.getWorkbook().createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    private void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    public static class ImportResult {
        private final java.util.List<String> successes = new java.util.ArrayList<>();
        private final java.util.List<String> errors = new java.util.ArrayList<>();

        public void addSuccess(String msg) { successes.add(msg); }
        public void addError(String msg) { errors.add(msg); }
        public java.util.List<String> getSuccesses() { return successes; }
        public java.util.List<String> getErrors() { return errors; }
        public boolean hasErrors() { return !errors.isEmpty(); }
        public int getSuccessCount() { return successes.size(); }
        public int getErrorCount() { return errors.size(); }
    }
}
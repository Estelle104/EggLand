package com.app.eggland.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Client;
import com.app.eggland.model.DetailVente;
import com.app.eggland.model.MvtStock;
import com.app.eggland.model.OeufProduction;
import com.app.eggland.model.PaiementSalaire;
import com.app.eggland.model.ProduitVente;
import com.app.eggland.model.StatutVente;
import com.app.eggland.model.Vente;
import com.app.eggland.repository.ClientRepository;
import com.app.eggland.repository.DetailVenteRepository;
import com.app.eggland.repository.MvtStockRepository;
import com.app.eggland.repository.OeufProductionRepository;
import com.app.eggland.repository.PaiementSalaireRepository;
import com.app.eggland.repository.ProduitVenteRepository;
import com.app.eggland.repository.StatutVenteRepository;
import com.app.eggland.repository.VenteRepository;

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
                row.createCell(4).setCellValue(m.getQuantite().intValue());
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
        if (excelData == null || excelData.length == 0) {
            result.addError("Fichier vide ou illisible.");
            return result;
        }

        StatutVente statutDefault = statutVenteRepository.findByCode("en_attente")
                .orElse(null);
        if (statutDefault == null) {
            result.addError("Statut de vente introuvable: en_attente");
            return result;
        }

        try (Workbook workbook = WorkbookFactory.create(new java.io.ByteArrayInputStream(excelData))) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter(Locale.FRANCE);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            int firstDataRow = resolveFirstDataRow(sheet, formatter, evaluator);

            for (int i = firstDataRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row, formatter, evaluator)) continue;
                try {
                    String clientNom = getStringCellValue(row.getCell(0), formatter, evaluator);
                    String produitCode = getStringCellValue(row.getCell(2), formatter, evaluator);
                    String quantiteStr = getStringCellValue(row.getCell(3), formatter, evaluator);
                    String prixStr = getStringCellValue(row.getCell(4), formatter, evaluator);

                    if (clientNom.isEmpty() || isCellBlank(row.getCell(1), formatter, evaluator) || produitCode.isEmpty() || quantiteStr.isEmpty() || prixStr.isEmpty()) {
                        result.addError("Ligne " + (i+1) + ": Données manquantes");
                        continue;
                    }

                    Client client = clientRepository.findByNomContainingIgnoreCase(clientNom)
                            .orElseThrow(() -> new IllegalArgumentException("Client introuvable: " + clientNom));

                    ProduitVente produit = produitVenteRepository.findByCode(produitCode)
                            .orElseThrow(() -> new IllegalArgumentException("Produit introuvable: " + produitCode));

                    LocalDate date = getDateCellValue(row.getCell(1), formatter, evaluator);
                    BigDecimal quantite = getDecimalCellValue(row.getCell(3), formatter, evaluator);
                    BigDecimal prix = getDecimalCellValue(row.getCell(4), formatter, evaluator);
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
            if (result.getSuccessCount() == 0 && result.getErrorCount() == 0) {
                result.addError("Aucune ligne de vente trouvée dans le fichier.");
            }
        }
        return result;
    }

    private int resolveFirstDataRow(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator) {
        Row firstRow = sheet.getRow(sheet.getFirstRowNum());
        if (firstRow == null || isRowEmpty(firstRow, formatter, evaluator)) {
            return sheet.getFirstRowNum() + 1;
        }

        String firstCell = getStringCellValue(firstRow.getCell(0), formatter, evaluator).toLowerCase(Locale.ROOT);
        String secondCell = getStringCellValue(firstRow.getCell(1), formatter, evaluator).toLowerCase(Locale.ROOT);
        String thirdCell = getStringCellValue(firstRow.getCell(2), formatter, evaluator).toLowerCase(Locale.ROOT);

        boolean looksLikeHeader = firstCell.contains("client")
                || secondCell.contains("date")
                || thirdCell.contains("produit")
                || thirdCell.contains("code");
        return looksLikeHeader ? sheet.getFirstRowNum() + 1 : sheet.getFirstRowNum();
    }

    private boolean isRowEmpty(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        for (int i = 0; i < 5; i++) {
            if (!isCellBlank(row.getCell(i), formatter, evaluator)) {
                return false;
            }
        }
        return true;
    }

    private boolean isCellBlank(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        return getStringCellValue(cell, formatter, evaluator).isEmpty();
    }

    private String getStringCellValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().format(DATE_FMT);
                }
                yield formatter.formatCellValue(cell).trim();
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> formatter.formatCellValue(cell, evaluator).trim();
            default -> "";
        };
    }

    private LocalDate getDateCellValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell != null && cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        }

        String value = getStringCellValue(cell, formatter, evaluator);
        for (DateTimeFormatter parser : List.of(DATE_FMT, DateTimeFormatter.ISO_LOCAL_DATE)) {
            try {
                return LocalDate.parse(value, parser);
            } catch (DateTimeParseException ignored) {
            }
        }
        throw new IllegalArgumentException("Date invalide: " + value + " (format attendu: dd/MM/yyyy)");
    }

    private BigDecimal getDecimalCellValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell != null && cell.getCellType() == org.apache.poi.ss.usermodel.CellType.NUMERIC) {
            return BigDecimal.valueOf(cell.getNumericCellValue());
        }

        String value = getStringCellValue(cell, formatter, evaluator)
                .replace("\u00a0", "")
                .replace(" ", "")
                .replace(",", ".");
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Nombre invalide: " + getStringCellValue(cell, formatter, evaluator));
        }
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

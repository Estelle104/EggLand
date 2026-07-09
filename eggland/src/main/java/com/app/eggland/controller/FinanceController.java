package com.app.eggland.controller;

import com.app.eggland.service.FinanceService;
import com.app.eggland.service.PdfExportService;
import com.app.eggland.service.ExcelExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Map;

@Controller
@RequestMapping("/admin/finance")
public class FinanceController {

    @Autowired
    private FinanceService financeService;

    @Autowired
    private PdfExportService pdfExportService;

    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping
    public String index(Model model) {
        LocalDate today = LocalDate.now();

        model.addAttribute("resume", financeService.getResumeFinancier());
        model.addAttribute("marge", financeService.getMarge());
        model.addAttribute("recettesParCategorie", financeService.getRecettesParCategorie());
        model.addAttribute("depensesParCategorie", financeService.getDepensesParCategorie());
        model.addAttribute("coutRevientParLot", financeService.getCoutRevientParLot());
        model.addAttribute("recettesMensuelles", financeService.getRecettesMensuelles12Mois());
        model.addAttribute("depensesMensuelles", financeService.getDepensesMensuelles12Mois());
        model.addAttribute("today", today.toString());
        model.addAttribute("todayMinus30", today.minusDays(30).toString());
        model.addAttribute("currentMonth", today.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));

        return "finance/index";
    }

    @GetMapping("/export/pdf/bon-livraison")
    public ResponseEntity<Resource> exportBonLivraisonPdf(@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) throws IOException {
        byte[] pdf = pdfExportService.generateBonLivraisonPdf(date);
        String filename = "bon_livraison_" + date + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdf));
    }

    @GetMapping("/export/pdf/paiement-salaire")
    public ResponseEntity<Resource> exportPaiementSalairePdf(@RequestParam("mois") @DateTimeFormat(pattern = "yyyy-MM") YearMonth mois) throws IOException {
        byte[] pdf = pdfExportService.generatePaiementSalairePdf(mois.atDay(1));
        String filename = "paiement_salaire_" + mois.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")) + ".pdf";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(new ByteArrayResource(pdf));
    }

    @GetMapping("/export/excel/production-oeufs")
    public ResponseEntity<Resource> exportProductionOeufsExcel(
            @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) throws IOException {
        byte[] excel = excelExportService.exportProductionOeufs(debut, fin);
        String filename = "production_oeufs_" + debut + "_" + fin + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(excel));
    }

    @GetMapping("/export/excel/mouvement-stock")
    public ResponseEntity<Resource> exportMouvementStockExcel(
            @RequestParam("debut") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate debut,
            @RequestParam("fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) throws IOException {
        byte[] excel = excelExportService.exportMouvementStock(debut, fin);
        String filename = "mouvement_stock_" + debut + "_" + fin + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(excel));
    }

    @GetMapping("/export/excel/paiements-salaires")
    public ResponseEntity<Resource> exportPaiementsSalairesExcel(
            @RequestParam("mois") @DateTimeFormat(pattern = "yyyy-MM") YearMonth mois) throws IOException {
        byte[] excel = excelExportService.exportPaiementsSalaires(mois.atDay(1));
        String filename = "paiements_salaires_" + mois.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")) + ".xlsx";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new ByteArrayResource(excel));
    }

    @PostMapping("/import/liste-ventes")
    public String importListeVentes(@RequestParam("file") MultipartFile file, Model model) {
        try {
            byte[] data = file.getBytes();
            ExcelExportService.ImportResult result = excelExportService.importListeVentes(data);
            model.addAttribute("importSuccesses", result.getSuccesses());
            model.addAttribute("importErrors", result.getErrors());
            model.addAttribute("importSuccessCount", result.getSuccessCount());
            model.addAttribute("importErrorCount", result.getErrorCount());
        } catch (IOException e) {
            model.addAttribute("importErrors", java.util.List.of("Erreur lecture fichier: " + e.getMessage()));
        }
        return index(model);
    }
}

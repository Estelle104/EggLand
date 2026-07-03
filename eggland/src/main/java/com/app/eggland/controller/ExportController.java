package com.app.eggland.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
public class ExportController {

    @GetMapping("/admin/exports")
    public String exportsPage(Model model) {
        LocalDate today = LocalDate.now();
        model.addAttribute("today", today.toString());
        model.addAttribute("todayMinus30", today.minusDays(30).toString());
        model.addAttribute("currentMonth", today.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));
        return "exports/index";
    }

    @GetMapping("/admin/exports/test")
    public String exportsTestPage() {
        return "exports/test";
    }

    @GetMapping("/admin/exports/vraitest")
    public String exportsVraiTestPage() {
        return "exports/vraitest";
    }
}

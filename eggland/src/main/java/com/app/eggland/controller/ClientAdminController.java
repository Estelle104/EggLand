package com.app.eggland.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.eggland.model.Client;
import com.app.eggland.service.ClientService;

@Controller
@RequestMapping("/admin/clients")
public class ClientAdminController {

    @Autowired
    private ClientService clientService;

    @GetMapping
    public String listeClients(Model model) {
        List<Client> clients = clientService.listeClient();
        List<Client> enAttente = clientService.listeClientsEnAttente();
        model.addAttribute("clients", clients);
        model.addAttribute("enAttente", enAttente);
        model.addAttribute("nbEnAttente", enAttente.size());
        model.addAttribute("pageTitle", "Gestion des clients");
        return "admin/clients";
    }

    @PostMapping("/accepter")
    public String accepter(@RequestParam("id") int id, RedirectAttributes ra) {
        try {
            clientService.accepterClient(id);
            ra.addFlashAttribute("success", "Client accepté avec succès.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/clients";
    }

    @PostMapping("/refuser")
    public String refuser(@RequestParam("id") int id, RedirectAttributes ra) {
        try {
            clientService.refuserClient(id);
            ra.addFlashAttribute("success", "Client refusé.");
        } catch (RuntimeException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/clients";
    }
}

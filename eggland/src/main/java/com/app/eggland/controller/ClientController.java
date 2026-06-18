package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.eggland.model.Client;
import com.app.eggland.service.ClientService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;




@Controller
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping("/")
    public String afficherLayoutClient() {
        return "client/layout";
    }
    

    @GetMapping("/client/inscription")
    public String afficherFormulaireInscription(Model model) {
        model.addAttribute("client", new Client());
        return "client/inscription";
    }

    @PostMapping("/client/inscription")
    public String postMethodName(@ModelAttribute("client") Client client, RedirectAttributes redirectAttributes) {
        try {
            clientService.registerClient(client);
            redirectAttributes.addFlashAttribute("successRegister", "Inscription effectuer");
            return "redirect:/client/layout";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorRegister", "problème d'inscription");
            return "redirect:/client/inscription";
        }
    }
    
    
}

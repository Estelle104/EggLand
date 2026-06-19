package com.app.eggland.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.app.eggland.model.Client;
import com.app.eggland.service.ClientService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;




@Controller
public class ClientController {
    @Autowired
    private ClientService clientService;

    @GetMapping("/")//vue en premier interraction
    public String afficherLayoutClient() {
        return "client/layout";
    }
    @GetMapping("/client/layout")// vue tant que client connecter 
    public String afficherVueClient() {
        return "client/layout";
    }
     
    

    @GetMapping("/inscription")
    public String afficherFormulaireInscription(Model model) {
        model.addAttribute("client", new Client());
        return "client/inscription";
    }

    @PostMapping("/client/inscription")
    public String postMethodName(@ModelAttribute Client client, HttpServletRequest request) {
        try {
            Client nouveauClient = clientService.registerClient(client);
            clientService.authentifierClientManuellement(nouveauClient.getEmail(),request);
            //dans le cas ou le client a un mot de passe à passer dans le formulaire
            // Client mdp = nouveauClient.getMotdePasse()
            // request.login(nouveauClient.getEmail(), mdp) request.login fait tout le travail de authentifierclientManuellement en une seule ligne
            return "redirect:/client/layout";
        } catch (RuntimeException e) {
            return "redirect:/client/inscription";
        }
    }
    
    
}

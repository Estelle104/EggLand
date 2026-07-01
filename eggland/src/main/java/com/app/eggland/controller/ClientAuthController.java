package com.app.eggland.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.app.eggland.model.Client;
import com.app.eggland.model.Livraison;
import com.app.eggland.service.ClientService;
import com.app.eggland.service.LivraisonService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;



@Controller
public class ClientAuthController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private LivraisonService livraisonService;

    @GetMapping("/")//vue en premier interraction
    public String afficherLayoutClient(Model model) {
        ajouterEmailClient(model);
        return "client/layout";
    }

    @GetMapping("/client/layout")// vue tant que client connecter 
    public String afficherVueClient(Model model) {
        ajouterEmailClient(model);
        return "client/layout";
    }

    @GetMapping("/client/livraison")
    public String afficherLivraisonsClient(Model model) {
        ajouterInfosLivraisonsClient(model);
        return "client/livraison";
    }

    private String ajouterEmailClient(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            model.addAttribute("userEmail", auth.getName());
            return auth.getName();
        }
        return null;
    }

    private void ajouterInfosLivraisonsClient(Model model) {
        model.addAttribute("livraisons", List.of());
        model.addAttribute("nbLivraisons", 0);

        String emailClient = ajouterEmailClient(model);
        if (emailClient != null) {
            List<Livraison> livraisons = livraisonService.listerLivraisonEnCoursPourClient(emailClient);
            model.addAttribute("livraisons", livraisons);
            model.addAttribute("nbLivraisons", livraisonService.compterLivraisonEnCoursPourClient(livraisons));
        }
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
            return "redirect:/client/espace/commandes";
        } catch (RuntimeException e) {
            return "redirect:/inscription";
        }
    }

    
    /*test */
    @GetMapping("/test-session")
    @ResponseBody
    public String test(HttpServletRequest request) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return "Authentification dans le contexte : " + (auth != null ? auth.getName() : "NULL");
    }
    
}

package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.app.eggland.model.Client;
import com.app.eggland.repository.ClientRepository;
import com.app.eggland.repository.LivraisonRepository;
import com.app.eggland.repository.VenteRepository;

@Controller
@RequestMapping("/client/espace")
public class ClientSpaceController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private VenteRepository venteRepository;

    @Autowired
    private LivraisonRepository livraisonRepository;

    private Client getClientConnecte() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || "anonymousUser".equals(auth.getPrincipal())) return null;
        return clientRepository.findByEmail(auth.getName()).orElse(null);
    }

    @GetMapping("/commandes")
    public String commandes(Model model) {
        Client client = getClientConnecte();
        if (client == null) return "redirect:/login";
        model.addAttribute("client", client);
        model.addAttribute("commandes", venteRepository.findByClientIdOrderByDateDesc(client.getId()));
        return "client/espace/commandes";
    }

    @GetMapping("/livraisons")
    public String livraisons(Model model) {
        Client client = getClientConnecte();
        if (client == null) return "redirect:/login";
        model.addAttribute("client", client);
        model.addAttribute("livraisons", livraisonRepository.findByClientIdOrderByDateLivraisonDesc(client.getId()));
        return "client/espace/livraisons";
    }

    @GetMapping("/profil")
    public String profil(Model model) {
        Client client = getClientConnecte();
        if (client == null) return "redirect:/login";
        model.addAttribute("client", client);
        return "client/espace/profil";
    }
}

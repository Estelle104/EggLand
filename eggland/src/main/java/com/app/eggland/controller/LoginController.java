package com.app.eggland.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.eggland.service.ClientService;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class LoginController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/redirection")
    public String redirectionParRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            // Extraction des autorités textuelles brutes (sans préfixe ROLE_)
            var autorites = auth.getAuthorities().stream()
                                 .map(a -> a.getAuthority())
                                 .toList();

            if (autorites.contains("admin")) {
                return "redirect:/admin/dashboard";
            } 
            else if (autorites.contains("gestionnaire")) {
                return "redirect:/gestion/dashboard";
            } 
            else if (autorites.contains("client")) {
                return "redirect:/client/layout";
            }
        }
        
        return "redirect:/login?error";
    }

    @PostMapping("/client/connexion")
    public String postMethodName(@RequestParam("email") String email, HttpServletRequest request) {
        try {
            clientService.connecterClient(email, request);
            return "redirect:/client/layout";
        } catch (RuntimeException e) {
            return "redirect:/login?error=" +e.getMessage();
        }
    }
    
    

}

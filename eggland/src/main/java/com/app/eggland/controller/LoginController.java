package com.app.eggland.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;


@Controller
public class LoginController {

    @GetMapping("/login")
    public String loginView() {
        return "login";
    }

    @GetMapping("/redirection")
    public String redirectionParRole(HttpServletRequest request) {
        if(request.isUserInRole("admin")){
            return "redirect:/admin/**";
        }
        else if (request.isUserInRole("gestionnaire")) {
            return "redirect:/gestion/stocks";
        } 
        else {
            return "redirect:/client/boutique"; 
        }
    }
    

}

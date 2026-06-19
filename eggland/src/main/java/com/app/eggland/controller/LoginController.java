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
            return "redirect:/admin/dashboard";
        }
        else if (request.isUserInRole("gestionnaire")) {
            return "redirect:/gestion/dashboard";
        } 
        else {
            return "redirect:/client/layout"; 
        }
    }
    
    

}

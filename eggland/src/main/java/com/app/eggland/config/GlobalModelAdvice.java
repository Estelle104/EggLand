package com.app.eggland.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.app.eggland.service.ClientService;

@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired
    private ClientService clientService;

    @ModelAttribute("nbClientsEnAttente")
    public long getNbClientsEnAttente() {
        return clientService.compterClientsEnAttente();
    }
}

package com.app.eggland.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Client;
import com.app.eggland.model.StatutClient;
import com.app.eggland.repository.ClientRepository;
import com.app.eggland.repository.StatutClientRepository;
import com.app.eggland.repository.UserAdminRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserAdminRepository userAdminRepository;
    @Autowired
    private StatutClientRepository statutClientRepository;

    @Transactional
    public Client registerClient(Client client){
        if(clientRepository.existsByEmail(client.getEmail()) || userAdminRepository.existsByEmail(client.getEmail())){
            throw new RuntimeException("Cet email est déjà utilisé par un autre compte.");
        }
        StatutClient statutActif = statutClientRepository.findByCode("actif")
                                                        .orElseThrow(() -> new RuntimeException("status actif introuvable"));
        client.setStatut(statutActif);
        client.setDateInscription(LocalDate.now());
        
        Client saveClient = clientRepository.save(client);
        
        return saveClient;
    }

    public void authentifierClientManuellement(String email, HttpServletRequest request) {
        // 1. Création du badge
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            email, // le mail du client
            null, // le mot de passe s'il y en a
            List.of(new SimpleGrantedAuthority("client")) //indique a spring que l'user est un client
        );
        // UsernamePasswordAuthentificationToken : créer un badge d'identité au nouveau client créer et il a besoin
        
        SecurityContextHolder.getContext().setAuthentication(authentication); // ici il dit a spring maintenant c'est un client
        
        /* Persistance dans la session HTTP*/
        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    }

    public void connecterClient(String email,HttpServletRequest request){
        Client client = clientRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("email not found"));

        if (client.getStatut() == null || !"actif".equalsIgnoreCase(client.getStatut().getCode())) {
            throw new RuntimeException("inactive");
        }
        this.authentifierClientManuellement(email, request);
    }

}

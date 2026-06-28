package com.app.eggland.service;

import java.time.LocalDate;
import java.util.List;

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
        // Nouveau client = statut "en_attente" (doit être accepté par admin)
        StatutClient statutEnAttente = statutClientRepository.findByCode("en_attente")
                                                        .orElseThrow(() -> new RuntimeException("statut 'en_attente' introuvable"));
        client.setStatut(statutEnAttente);
        client.setDateInscription(LocalDate.now());
        
        Client saveClient = clientRepository.save(client);
        
        return saveClient;
    }
    
    @Transactional
    public Client accepterClient(int clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        StatutClient statutActif = statutClientRepository.findByCode("actif")
                .orElseThrow(() -> new RuntimeException("statut 'actif' introuvable"));
        client.setStatut(statutActif);
        return clientRepository.save(client);
    }
    
    @Transactional
    public Client refuserClient(int clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client introuvable"));
        StatutClient statutInactif = statutClientRepository.findByCode("inactif")
                .orElseThrow(() -> new RuntimeException("statut 'inactif' introuvable"));
        client.setStatut(statutInactif);
        return clientRepository.save(client);
    }
    
    public long compterClientsEnAttente() {
        return clientRepository.findByStatut_Code("en_attente").size();
    }
    
    public List<Client> listeClientsEnAttente() {
        return clientRepository.findByStatut_Code("en_attente");
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
        
        /* Persistance dans la session HTTP donc la session client*/
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

    public List<Client> listeClient() {
        return clientRepository.findAll();
    }

    public Client trouverClientParId(int id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Transactional
    public Client trouverOuCreerClientParNom(String nom) {
        if (nom == null || nom.isBlank()) {
            throw new RuntimeException("Veuillez saisir un nom de client.");
        }

        // Chercher par correspondance exacte (insensible à la casse)
        List<Client> clients = clientRepository.findByNomContainingIgnoreCase(nom.trim());
        if (clients.size() == 1) {
            return clients.get(0);
        }

        // Créer un nouveau client
        StatutClient statutActif = statutClientRepository.findByCode("actif")
                .orElseThrow(() -> new RuntimeException("statut 'actif' introuvable"));

        Client nouveau = Client.builder()
                .nom(nom.trim())
                .dateInscription(LocalDate.now())
                .statut(statutActif)
                .build();
        return clientRepository.save(nouveau);
    }
}

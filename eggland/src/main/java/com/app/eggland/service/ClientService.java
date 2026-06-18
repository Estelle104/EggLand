package com.app.eggland.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Client;
import com.app.eggland.repository.ClientRepository;
import com.app.eggland.repository.UserAdminRepository;

import jakarta.transaction.Transactional;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private UserAdminRepository userAdminRepository;

    @Transactional
    public void registerClient(Client client){
        if(clientRepository.existsByEmail(client.getEmail()) || userAdminRepository.existsByEmail(client.getEmail())){
            throw new RuntimeException("Cet email est déjà utilisé par un autre compte.");
        }
        clientRepository.save(client);
    }

}

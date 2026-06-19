package com.app.eggland.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.eggland.model.Client;
import com.app.eggland.model.UserAdmin;
import com.app.eggland.repository.ClientRepository;
import com.app.eggland.repository.UserAdminRepository;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService { 
    @Autowired
    private UserAdminRepository userAdminRepository;
    @Autowired
    private ClientRepository clientRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{ 
        Optional<UserAdmin> userAdminOptional = userAdminRepository.findByEmail(email);
        Optional<Client> userClientOptional = clientRepository.findByEmail(email);

        
        
        if(userAdminOptional.isPresent()){
            UserAdmin useradmin = userAdminOptional.get();
            String Rolename = useradmin.getRole().getCode();// pour admin et gestionnaire seulement pas pour client
            return User.builder()
                    .username(useradmin.getEmail())
                    .password(useradmin.getMotDePasse())
                    .authorities(Rolename)
                    .disabled(!useradmin.getActif())
                    .build();
        }

        if(userClientOptional.isPresent()){
            Client client = userClientOptional.get();
            return User.builder()
            .username(client.getNom())
            .password("")
            .authorities("client")
            .build();
        }
        throw new UsernameNotFoundException("Aucun utilisateur trouvé avec l'email : " + email);        
    } 
}

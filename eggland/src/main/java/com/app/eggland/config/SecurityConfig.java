package com.app.eggland.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/","/client/accueil","/inscription","/client/inscription","/css/**","/js/**","/images/**","/test-session").permitAll() //tout le monde peut voir l'accueil et l'inscription en tant que client
            .requestMatchers("/client/connexion","/login","/redirection").permitAll()
            .requestMatchers("/admin/**").hasAuthority("admin")// l'admin a acces a ses fonctionnalités et toute url contenant layout et liste
            .requestMatchers("/lots/**","/gestionnaire/**").hasAnyAuthority("gestionnaire","admin")// gestionnaire a acces aux url de lots et gestionnaire définissez en fonction de vos besoin
            .requestMatchers("/client/**").hasAnyAuthority("client","admin")// le client a acces aux fonctionnalité de l'url /client
        .anyRequest().authenticated()
       )
       .formLogin(form -> form
            .loginPage("/login")//url ireo 
            .defaultSuccessUrl("/redirection")
            .permitAll()
       )
       .logout(out ->out
            .logoutUrl("/logout") // L'URL qui déclenche la déconnexion
            .logoutSuccessUrl("/") // Où on va après s'être déconnecté
            .invalidateHttpSession(true) // Efface la session HTTP du serveur
            .clearAuthentication(true) // Efface le badge de sécurité de Spring
            .permitAll()
        )
       .csrf(csrf -> csrf.disable());

       return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawpassword){
                return rawpassword.toString(); // retourne le mot de passe sans hashage
            }
            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword){
                return rawPassword.toString().equals(encodedPassword); // Comparaison simple
            }
        };
    }
}

package com.app.eggland.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
            .requestMatchers("/","/client/inscription","/css/**","/js/**","/images/**").permitAll() //tout le monde peut voir l'accueil et l'inscription en tant que client
            .requestMatchers("/admin/**","/**/layout/**","/**/liste/**").hasRole("ADMIN")// l'admin a acces a ses fonctionnalités et toute url contenant layout et liste
            .requestMatchers("/lots/**","/gestionnaire/**").hasRole("GESTIONNAIRE")// gestionnaire a acces aux url de lots et gestionnaire
            .requestMatchers("/client/**").hasRole("CLIENT")// le client a acces aux fonctionnalité de l'url /client
        .anyRequest().authenticated()
       )
       .formLogin(form -> form
            .loginPage("/login")
            .defaultSuccessUrl("/redirection")
            .permitAll()
       )
       .logout(out ->out
            .logoutSuccessUrl("/")
            .permitAll()
       )
       .csrf(csrf -> csrf.disable());

       return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}

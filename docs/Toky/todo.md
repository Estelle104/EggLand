## Tâche

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T1.1 | `SecurityConfig` : formLogin, routes protégées, rôles ADMIN/GESTIONNAIRE | `config/SecurityConfig.java` | 1h |
| T1.2 | `UserDetailsServiceImpl` + BCrypt (optionnel) + gestion sessions | `service/UserDetailsServiceImpl.java`, `config/SecurityConfig.java` | 1,5h |
| T1.3 (Optionnel) | CRUD UserAdmin (liste, créer, activer/désactiver) | `UserAdminController.java`, `UserAdminService.java`, `UserRepository.java` | 1,5h |
| T1.4 | Auth client séparée : login + inscription + session client | `ClientAuthController.java`, `ClientService.java`, `ClientRepository.java` | 1h |
| T1.5 | Templates : `login.html` admin + `client/inscription.html` + page users admin | `templates/login.html`, `templates/client/inscription.html` | 2h |

**Total : 7h — 2,5 jours (3h/jour)**

---
<br>

## Tâche additionnelle 1 : 

| Code | Tâche | Fichiers à utiliser |
|------|-------|---------------------|
| T1.6 | `SecurityConfig` : ajout de route /admin/login | `config/SecurityConfig.java` + `controller/LoginController.java` + `stafflogin.html` |
| T1.7 | `sidebar.html` : faire fonctionner le bouton déconnection et afficher l'utilisateur en session | `fragments/sidebar.html` |

---

## Tâche additionnelle 2 :
| Code | Tâche | Fichiers à utiliser |
|------|-------|---------------------|
| T1.8 | `pagination`: effectuer la pagination de toute les listes | `*.java`: qui contient toute les affichages de liste bien sur + `*.html`: qui ont pour fonction,l'affichage des listes | 
---

## Rêgle d'organisation :
<br>

***T1.1, T1.6 : mise en place de la gestion des routes ``SecurityConfig.java  répertoire : /config``***
-`.authorizeHttpRequests` : Définit les droits d'accès aux URL.
    - `requestMatchers("/url/")` Définit les chemins protégés ou publics.
    - `hasAnyAuthority()`: Restreint l'accès aux rôles spécifiés (ex: "ADMIN")
    - `permitAll()`: Rend une route accessible à tous
    - `.anyRequest().authenticated()`: orce l'authentification pour tout autre accès.


- `.formLogin`: Configure la page de login personnalisée et la redirection après succès.
    - `.loginPage`: dirige dans la page selon la route /login
    -  `.defaultSuccessUrl`: dirige l'utilisateur vers les routes correspondant à leur rôle
    - `.permitAll()`: permet a n'importe qui même sans session de l'utilisé

- `.logout`: gère la fin de session, l'invalidation de la HttpSession et le nettoyage du SecurityContext
    - `.logoutUrl` : l'url de deconnexion
    - `.logoutSuccessUrl` : la page d'arrivé lors de la déconnexion : ici page vitrine 
    - `.invalidateHttpSession` : effacer la session
    - `.clearAuthentication` : efface le badge de sécurité de spring-boot
    - `.permitAll` : permet a n'importe quel rôle d'effectuer cet opération

---
<br>

***T1.2 :Utilisation de la configuration de ``UserDetailsServiceImpl.java répertoire : /service et mise en place des repository correspondant``***
- mise en place des repository (UserAdminRepository.java , ClientRepository)
    * Utilisation de Optional<T> : Gère le cas où l'entité est absente (évite les NullPointerException)
    * la fonction ``.get()`` permet de prendre directement les attributs des classe via les repository
    * `.builder()` : Design pattern utilisé pour construire l'objet UserDetails de Spring Security de manière lisible, en mappant les rôles (autorités) et les identifiants.
---
<br>

***T1.3: Optionnel***
--- 

<br>

***T1.4: formulaire de connexion client et utilisateur***
- ClientService.java : 
    * `existsByEmail()` : Vérification d'unicité dans ClientRepository (type boolean)
    * `Authentification , new UsernamePasswordAuthenticationToken(mail,mdp,Liste des roles autorisé)` : Création manuelle d'un UsernamePasswordAuthenticationToken
    * `SecurityContextHolder.getContext().setAuthentication(authentication);`: : Force l'authentification dans le contexte Spring.
<br> 

 ```java
 /* Persistance dans la session HTTP donc la session client :ici gestion des sessions*/
     HttpSession session = request.getSession(true);
     session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
 ```
<br>

```java
public void connecterClient(String email,HttpServletRequest request){
        Client client = clientRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("email not found"));

        if (client.getStatut() == null || !"actif".equalsIgnoreCase(client.getStatut().getCode())) {
            throw new RuntimeException("inactive");
        }
        this.authentifierClientManuellement(email, request);
    }
```
<br>

levée d'exeption dans le cas où le client est inactive: ``RuntimeException`

- ClientAuthController.java + LoginController.java + UserDetailsServiceImpl.java: 
    * `clientService.authentifierClientManuellement(nouveauClient.getEmail(),request);` : permet de mettre le nouveau client en session immédiate après inscription
    * `@ModelAttribute Client client`:Lie les données du formulaire à l'objet Client
    * `LoginController.java: `: la méthode `String postMethodName(@RequestParam("email") String email, HttpServletRequest request)` connecte le client s'il a un compte (via la ligne clientService.connecterClient())
    * `UserDetailsServiceImpl.java  ` la partie avec if avec le .password(useradmin.getMotDePasse()) c'est le moyen de connexion pour l'admin et les gestionnaires , le .authorities(rolename) permet de prendre leurs role et l'utilisé dans ce block
```java
Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth != null) {
            // Extraction des autorités textuelles brutes (sans préfixe ROLE_)
            var autorites = auth.getAuthorities().stream()
                                 .map(a -> a.getAuthority())
                                 .toList();

            if (autorites.contains("admin")) {
                //return "redirect:/admin/dashboard"; ici décommenté si vous avez finis de mettre admin 
                return "redirect:/races"; // ici test pour voir s'il fonctionne
            } 
            else if (autorites.contains("gestionnaire")) {
                return "redirect:/races";//ici n'oubliez pas non plus le /gestion/dashboard si ce n'est pas fait comme pour l'admin
            } 
            else if (autorites.contains("client")) {
                return "redirect:/client/espace/commandes";
            }
        }
```

***T1.5, T1.7***
* `client/layout.html` : site vitrine + vue des clients connecter avec leur email afficher qui montre un dropdown `Deconnexion` + `Mon Espace` 
* `client/inscription.html` : formulaire d'inscription des futurs client 
* `login.html` : page de connexion client 
* `stafflogin.html` : page de connexion pour le backoffice 

***T1.8***
* ``Pagination.html`` : refactorisation de pagination de sorte que toute les classes puisse bénéficié des pagination
* ``PaginationUtils.java`` : généralisation de la pagination 
* 
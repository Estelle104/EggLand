# github
- une fonctionnalite correspond a une branche 
    - pour chaque branche il faut faire plusieurs commit 
- Tester les fonctionnalites faites avant de faire le pull request
- Estelle seule qui doit faire le merge, tout le monde doit juste faire des pull request
- Toujours faire git pull avant git push


**Ma part** 
## Toky | Auth & Sécurité

| Code | Tâche | Fichiers à utiliser | Durée |
|------|-------|---------------------|-------|
| T1.1 | `SecurityConfig` : formLogin, routes protégées, rôles ADMIN/GESTIONNAIRE | `config/SecurityConfig.java` | 1h |
| T1.2 | `UserDetailsServiceImpl` + BCrypt (optionnel) + gestion sessions | `service/UserDetailsServiceImpl.java`, `config/SecurityConfig.java` | 1,5h |
| T1.3 (Optionnel) | CRUD UserAdmin (liste, créer, activer/désactiver) | `UserAdminController.java`, `UserAdminService.java`, `UserRepository.java` | 1,5h |
| T1.4 | Auth client séparée : login + inscription + session client | `ClientAuthController.java`, `ClientService.java`, `ClientRepository.java` | 1h |
| T1.5 | Templates : `login.html` admin + `client/inscription.html` + page users admin | `templates/login.html`, `templates/client/inscription.html` | 2h |

**Total : 7h — 2,5 jours (3h/jour)**

---

# correction de mes taches
* on abandonne le users.html
* voici la nouvelle tâche a faire 
    - le login sera au couleur et 
    - les champs ( email et password ) seulement 
* Demander à Gemini comment faire le pull request dans Github et le merge car je suis le second a pouvoir merge


### Dans .gitignore
inclure dedans 
---
    docker-compose.yml <br>
    Dockerfile <br>
    .devcontainer <br>
    <br>
    src/main/resources/application-local.properties #à effacer <br>
    src/main/resources/application.properties <br>
    instruction.md <br>
---


# Documents utile pour la présentation du projet actuel :

* un livre(autre que les truc trivial) 
    - table des matières 
        * titre de tableau ne pas mettre dans la table des matières 
        * les images pas dans la table des matières 
    - table des matière 
        * liste des tables (voir quels sont les tables dans le truc)
        * liste des figures 
    - un couverture (avec le logo ITU(ici) car tu n'as aucun droit java oui mais bon)

* présentation du projet
    - contexte(le truc dont on va parler)
    - problèmatique(le projet doit avoir une raison d'éxisté)
    - solution (l'apport de votre projet "c'est pouquoi nous avons créer ce projet qui ...(points essentiel))

* Cahier De Charge

* cahier des charges fonctionnelle (cahier de toute les fonctionnalités demandé par le client):  C.D.C.F
    - CDCF une part du Cahier De Charge
    - liste des fonctionnalités demandé
    - critère de validation(si je fais ci il doit faire tel action /passer d'un état à un autre)
    - CHOIX TECHNOLOGIQUE(java/php/postgres/mysql)
        * arguments: 
          * a éviter : raison perso ,dire que c'est performant / tendance / facilite le projet /c'est cher payer
          * le choix se fait par rapport au projet :    
            - CRITERE : bibliothèque(approche par rapport au coùt) mysql peut le faire ,
                si fast food une base puissante pour gérer les  commandes choix:oracle (approche par efficacité & performance)  
        * alternative:
            - CRITERE : avec des raisons et des raisons et les ++ qui va avec qui le concurence avec l'autre 
  
    - + tableau récapitulatif qui rappel tout : vers la fin on met le choix final
    - dans la présentation faite un récapitulatif sur l'architecture et utilise le logo / image(AVEC DES LÉGENDES) 
  

* PLANNING (utilisé ganttproject):tel dev fait quoi et quand <br>
le planing doit être inclus dans le livre sous forme de photo d'une liste mais pas la photo du graphe qui sera floue<br>
faite un capture d'écran de la liste et ajouté les LEGENDE EN PRECISANT de x date a y date 

> un mois de travail = 22 jours donc 26 jours = 1mois +4 jours
dans le planing mettez correctement qui a fait tel ou tel truc même quant il s'agit de faire 
* le livre(pour la soutenance) dont nous parlons [maintenant](#documents-utile-pour-la-présentation-du-projet-actuel-)
* précisé tout ce que vous avez fait de l'analyse jusqu'au codage

recherche : principe SOLID

* BUDGET : (en fonction du planing + dans le livre)
- coût estimatif des dev
- coût estimatif des matériels qui sont nécéssaire 




# LES CRITÈRES DE VALIDATION : (GRANDE IMPORTANCE)
Les listes doivents comprendre des recherches multicritère : 
- par mot clé

Les chiffres doivent être en intervalle 
Les catégories en `<select>` 
Pagination modifiable a la demande n pages ? n ligne ? 
les tris 
- décroissant 
- croissant

mettre des imports et export en excel 
ATTENTION :
Import de fichier  excel
* mettre les titres et description sur l'endroit de l'exportation
* toujours rediriger vers les listes 
* gérer les doublons (comment faire pour considérer qu'une donné est en double)
* gérer les erreurs(import annulé et précisé aux personnes où se trouve l'erreur) 
* il faut vérifier les noms des colonnes (vérification dans les codes bien sûr)
Export de fichier excel


Export pdf(mettez le en beau gosse)

Tableau statistique(GRAPHE)
* afficher les indices de performance
* top 5 :  
  - le truc est besoin par le client
  - le truc n'est pas utile pour les admin (il veut plutôt une courbe qui change selon les produits choisi)
* courbe 
* histogramme
* secteur(le camembert)
* ajouté un filtrage par date x à date y sur tel produit ou tel prix ou un filtrage par tel date précise


# pour la présentation :
- mains hors des poches
- présentation orienté projet et non sur la tech(mais pour l'instant c'est orienté fonctionnel)
- soyez présentable
- donnez au juré de l'eau avec le livre 
- souriez pour la présentation(un sourir peux tout changer)
- entre 19 et 20 minute pour présentation idéale pour avoir une bonne note (parler bien et calmement)
- pour l'aléa : un seul effectue l'épreuve les autres attendent
- 8 minutes pour la démonstration 
- démo vidéo conseiller (on peut posé pour résoudre un problème)

# POUR MOI QUI A PEUR: 
si tu as oublié ce que tu dois dire : 
* dit des choses ayant une relation avec le projet 
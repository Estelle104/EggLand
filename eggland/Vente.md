# TODO module de vente 

## Deroulement
    - Faire un formulaire pour creer une vente par produit ,
        cela se fera bien sur par client ,car un client va faire l'achat
## Backend

## T7.1
## template/vente/
    - formulaire.html
        - Creation de vente : faire un formulaire pour creer une nouvelle 
        vente
        - [] prendre l'id de vente (type hidden) 
            - Le formulaire contiendra 
                - [ok] Produit (id )
                - [ok] quantite
                - [ok] id_client (A ajouter)
                - [ok] id_vente
                - [ok] Total

    - listeVente.html
        - [] on affiche les ventes existant
        - avec les bouttons suivants
            - [] supprimer        
            - [] modifier        
            - [] voir details (detailVente.html)
        - on envoie toujours l'id de vente et du client

    - detailVente.html
        - [] on afficher les detailVente.html dans un pop up avec tous les infos necessaire
        
    - formulaireModificaiton.html
        - [] une formulaire de modification

## controller/VenteController
    - Ici on va traiter les donnees envoyer via le formulaire de vente 
    - VenteController
        - GetMapping("/ventes")
            - [ok] on redirige dans "/ventes/listeVente"

        - GetMapping("/ventes/listeVente")
            - [ok] prendre la liste des vente existant
            - [ok] envoyer cette liste a la page "vente/listeVente.html"


        - GetMapping("/ventes/creation")
            - [ok] on creer une liste de lot (LotService)
            - [ok] prendre l'id du client et on le mets dans une vente
            - [ok] prendre les liste de produit (oeuf , poulet , fumier) 
            - [ok] Creer une vente (l'id du client sera deja la)
            - [ok] envoyer ce vente au formulaire
            - [ok] envoyer cette id au formulaire
            - [ok] envoyer cette liste au formulaire
            - [ok] le formulaire est "vente/formulaireCreation"


        - PostMapping("/ventes/creation")
            - [] Prendre les details de vente inserer dans le formulaire 
            - [] appeler fonction enregistrerVente() dans "service/VenteService.java"
            - [ok] on renvoie a "/ventes"
        
        - PostMapping("/ventes/modifier")
            - [ok] on prend tous les infos du vente
            - [ok] on envoie dans "ventes/formulaireModif"

        - PostMapping("/ventes/supprimer")
            - [ok] prendre l'id du vente
            - [ok] supprimer ce vente 
            - [ok] renvoier dans "/ventes"

        - PostMapping("/ventes/detail")
            - [] En prend l'id de vente et du client 
            - [] On creer les details de cette vente 
            - [] On envoie les details dans "vente/detailVente.html"

## service/VenteService
    - La on aura les traitements de la base de donnees et les autres fonctionnaliter importante
    - [ok] saveDetailVente()
    - [ok] saveVente()
    - [ok] saveMvtArgent() si il n'existe pas encore vo foronina de ny type atao entrer de ny categorie atao vente
    - [ok] listeVente()
    - [ok] listeDetailVente(id client ,id vente)
    - [ok] supprimerVente(id)
    - [ok] findVenteById(id)
    - [ok] updateDetailVente(id_vente)
    - [ok] supprimerDetailVente(id vente,id detail)
    - [ok] prendre liste de produit
    - [] enregistrerVente() :
            - [] critere de validation : voir le stock d'oeuf est encore suffisant sinon envoie message d'erreur(A regarder dans )
            - [] Prendre l'id du client 
            - [] sauver les details de vente (detailVenteService) 
            - [] si le produit = "poulet"
                - [] on reforme le lot (service/LotService) vue que le nombre de poule dans ce lot va diminuer
            - [] ajouter la vente 
            - [] Ajouter le vente dans  mouvement Argent (avec type=entrer ,et categorie = "vente" dans service/MvtArgentService.java) 

## Autre 
    - Pour le formulaire de vente 
        - [ok] on aura besoin d'un boutton + pour ajouter une nouvelle ligne cela se fera dans vente.js
        - [ok] on ajoutera aussi si besoin la liste de lot si le produit choisis est le poulet
        - [ok] Ajuste le css en consequence
        - [ok] Ajouter les fragments de page  
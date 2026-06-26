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
                - [] Produit (id )
                - [] quantite
                - [] prix unitaire 
                - [] id_client (A ajouter)
                - [] id_vente
                - [] Total

    - listeVente.html
        - [] on affiche les ventes existant
        - avec les bouttons suivants
            - [] supprimer        
            - [] modifier        
            - [] voir details
        - on envoie toujours l'id de vente et du client

    - detailVente.html
        - [] on affiche les details d'une vente
        
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
            - [] on creer une liste de lot (LotService)
            - [] prendre l'id du client et on le mets dans une vente
            - [] prendre les liste de produit (oeuf , poulet , fumier) 
            - [] Creer une vente (l'id du client sera deja la)
            - [] envoyer ce vente au formulaire
            - [] envoyer cette id au formulaire
            - [] envoyer cette liste au formulaire
            - [] le formulaire est "vente/formulaireCreation"


        - PostMapping("/ventes/creation")
            - [] Prendre les details de vente inserer dans le formulaire 
            - [] Prendre l'id du client 
            - [] sauver les details de vente 
            - [] si produit = "poulet"
                - [] on reforme le lot 
            - [] ajouter la vente 
            - [] Ajouter le mouvement Argent (si payer) 
            - [] on renvoie a "/ventes"
        
        - PostMapping("/ventes/modifier")
            - [] on prend tous les infos du vente
            - [] on envoie dans "ventes/formulaireModif"

        - PostMapping("/ventes/supprimer")
            - [] prendre l'id du vente
            - [] supprimer ce vente 
            - [] renvoier dans "/ventes"

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


## Autre 
    - Pour le formulaire de vente 
        - [] on aura besoin d'un boutton + pour ajouter une nouvelle ligne cela se fera dans vente.js
        - [] on ajoutera aussi si besoin la liste de lot si le produit choisis est le poulet
        - [] Ajuste le css en consequence
        - [] Ajouter les fragments de page  
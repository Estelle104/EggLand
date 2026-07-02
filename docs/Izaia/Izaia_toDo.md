# Guide  pour la vente 

## Backend 
### Controller
    - VenteController.java: il y a les routes pour tous ce qu'il y a en rapport avec la vente (Get + Post )

 **Duree estime :1h 30**
* [X] Creation de vente 
* [X] Lister les ventes
* [X] Lister les ventes

**Fichiers :**
* `VenteService`
* `VenteController`
* `VenteService`
* `MvtArgent`
* `OeufService`

---

### T7.1 & T7.2 - VenteServices  
* [X] VenteService.java : il y a les fonctionnalites suivantes
    * [X] saveVente() : pour sauver une vente 
    
    * [X] listeVente() : pour prendre les listes des ventes deja fait 
        
    * [X] supprimerVente() : pour supprimer une vente 
        
    * [X] trouverVenteParId() : pour chercher une vente par son Id 
        
    * [X] listeProduitVente() : pour prendre tous les produits en vente 
        
    * [X] trouverProduitVenteParId() : pour avoir le produit grace a son id de vente
        
    * [X] listeDetailVente() : pour prendre tous les listes de detail de vente 
        
    * [X] enregistrerVente() : ici c'est pour enregistrer notre vente a l'interieur on trouvera quelque fonction cle comme (reformerUnLot ou retirerDuStock pour updater un lot et le stock) aussi on y creeEntrer() dans le mouvement argent 
        
    * [X] enregistrerModificationVente() : pour enregistrer les modification sur les details de vente et la vente fait par le client 

* [X] On a aussi du ajouter quelque fonctionnalite dans d'autre fichier comme 
    * [X] retirerDuStock() : dans OeufService.java (deja demander la permission )

        et 

* [X] ajouterAuStock() : ces deux fonction sont utile dans la creation et la modification d'une vente

* [X] On a aussi creer quelque repository (ProduitVente )

### T7.3 - VenteService & MvtArgent
* [X] Ajout du vente dans MvtArgent

### Model 
* [X] ajout d'une colonne client_id dans le "DetailVente.java" 

## FrontEnd 
**Fichiers :**

* `templates/ventes/derailVente.html`
* `templates/ventes/formulaireCreation.html`
* `templates/ventes/formulaireModification.html`
* `templates/ventes/listeVente.html`

---
### T7.4 - templates/vente
* [X] formulairecreation.html  :c'est le formulaire ou on ajoute notre detail de vente 
* [X] la date de vente est automatiquement mis pour le jour meme (modifiable si c'est necessaire )
* [X] listeVente() : ici on affiche nos ventes avec les actions possible(voir detail,modifier , supprimer)
* [X] detailVente.html() : ici il y a les infos de la vente 

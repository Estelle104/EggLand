# Test 1
- **Date :** 29-06-26
- **Heure :** 23:00
- **Testeur :** CP - ETU004185
- **Jeu de donnee :** 

    - **Module batiment**
        - CRUD de batiment :
            - *Resultat attendu :* 
                * create : Un nouveau batiment inséré dans la base
                * update : batiment mis a jour
                * delete : batimenr supprime
                * list : liste des batiments

            - *Reponse :* **OK**
        

    - **Module race**
        - CRUD de race :
            - *Resultat attendu :* 
                - create : Un nouveau race insérée dans la base
                - update : race mis a jour
                - delete : race supprime
                - list : liste des races

            - *Reponse :* **OK**


    - **Module lot**
            - CRUD de lot :
                - *Resultat attendu :* 
                    - create : Un nouveau lot insérée dans la base qui peut contenir plusieurs race et appartient a un batiment 
                
                - *Reponse :* **NON**


    - **Module oeuf**
        - CRUD de oeuf :
            - *Resultat attendu :* 
                * create : Un nouveau oeuf insérée dans la base et la date de collecte ne doit pas depasser le jour d'entree de lot
            - *Reponse :* **OK**
                
                * on peut ajouter plusieurs status d'oeuf au moment d'entree (x casses, y consommes)
            - *Reponse :* **OK**
                
                * historique des oeufs avec les filtres et l'export en xlsx
            - *Reponse :* **OK**
            
                * affichage de taux de ponte 
            - *Reponse :* **Erreur de calcul**
    
    
    - **Module nourriture**
        -CRUD de nourriture :
            - *Resultat attendu :* 
                -create: Une nouvelle nourriture inseree dans la base avec son seuil d'alerte (verfication de la nourriture ne doit etre inferieur au seuil)
            - *Reponse :* **OK**
                - update : nourriture mis a jour
            - *Reponse :* **OK**
                - delete : nourriture supprime
            - *Reponse :* **OK**
                - list : liste des nourriture
            - *Reponse :* **OK**

                
                
    - **Module stock**
        - Sortie nourriture
            - *Reponse :* **Non ok** : 
                - Possible alors que quantite de nourriture vide (quantite negatif)
                - On peut faire un entree dans le futur
        - Entree nourriture
            - *Reponse:* **Non ok** : 
                - On peut faire un entree dans le futur
        - Historique
            - *Reponse:* **Ok** 
    - **Module finance**
        - Cadre d'information
            - **Ok**
        - Barchart 
            - **Ok**
        - Repartition
            - **Non ok** :
                - titre de chaque categorie de depense/recette a modifier
        - Cout de revient par lot
            - **?**
        
    - **Module export/import**
        - Importation des ventes
            - **A voir**
        - Exportation pdf de bon livraison a une date
            - *Reponse :* **OK**
        - Exportation excel d'oeufs
            - *Reponse :* **Erreur**
                - Erreur par rapport aux races
        - Exportation excel mouvement stock
            - *Reponse :* **Ok**
        - Exportation excel paiement salaire
            - *Reponse :* **Non ok** : 
                - sans detail des autres paiement (juste le dernier)
        
    - **Module livraisons**
        - Insertion Livraison
            - *Reponse :* **Non ok** :
                - On peut encore choisir le statut
        - Liste des livraisons
            - *Reponse :* **Non ok** :
                - Le statut devrait etre modifiable
        - Filtre
            - *Reponse :* **Ok**
        - Recherche par nom de client
            - *Reponse :* **Ok**
        
    - **Module vente**
        - Insertion Vente
            - *Reponse :* **Non ok** : 
                - Le nombre de poulet du lot, apres vente ne change pas
                - On peut vendre des poules dans des lot encore actif (a discuter)
        - Liste vente
            - *Reponse :* **Ok**
        - Details vente
            - *Reponse :* **Ok**
        - Modification Vente
            - *Reponse :* **Non ok**
                - Meme issue que l'insertion mais modification marche
    


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
        -
        
    - **Module finance**
        -
        
    - **Module export/import**
        -
        
    - **Module livraisons**
        -
        
    - **Module vente**
        -
    


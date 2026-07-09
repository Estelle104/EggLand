# Toky | Authentification & Sécurité

## Critêre de validité :
| # | Fonctionnalité |  Critère de validation |
|---|----------------|------------------------|
|V1 |l'utilisateur entre dans le site vitrine ne possèdant aucune session : un futur client peut-être | la vue du site vitrine |
| V2| l'utilisateur vas soit dans connexion et doit entrer ces identifiants ou bien `S'inscrire` dans le cas ou il n'en a pas, ce sera le même s'il a parcouru le site vitrine il y a le même bouton s'inscrire | la vue du formulaire d'inscription en tant que client |
|V3 | un utilisateur peut s'inscrire dans le formulaire déstiné au client et se connecter automatiquement | la table `client` enregistre + page layout pour client avec son mail + deconnexion apparait si on veut se déconnecter + bouton Mon Espace |
| V4| administateur + gestionnaire : on peut se connecter dans le bouton de connexion du site vitrine via `acces administrateur et gestionnaire` | le formulaire s'affiche + verification des identifiant dans `useradmin` + affichage de la page dans `admin/dashboard` |
| V5| l'email des gérants sont afficher dans les pages qui leurs sont attribué | le sidebar affiche l'email de l'administrateur ou du gestionnaire avec le bouton déconnecter à côté |
| V6| chaque liste peux êtres paginné : ``PaginationUtils.java`` , ``fragments/pagination.html``| les listes dispose d'un petit input de type number que n'importe quel utilisateur peux saisir ou choisir selon les flèches montante ou descendante + limite de sécurité d'affichage d'au moins 1 lignes |

## Rêgle de gestion :
* un client ne possède que son email pour vérifier son identité 
* une session se créer lorsque le client s'inscrit pour la première fois  et dans les prochaines visite du site
* les gérant ont un mot de passe qui leur donne accès au backoffice du site 
* chaque liste dispose de pagination qui fonctionne avec les filtres 
* l'utilisateur peut mettre manuellement le nombre de ligne a affiché pour chaque liste ou le choisir sur la flèche de l'input number
* une limite de validité (limite de ligne a affiché est gérer dans le controller pour le size)

## Rêgle d'organisation : 
- **View : une view pour un objectif**
- ``client/layout.html`` : site vitrine, site de vue des clients connecter
- ``client/inscription.html`` : page d'inscription pour les futurs client
- ``login.html`` : page d'inscription pour les clients
- ``stafflogin.html`` : page d'inscription pour les personnels tel administrateur,gestionnaire

- **séparation de service , repository , controller**
- ``Controller`` : appelle les services pour les inscriptions et la connexion et a une exception près : les controlles de la ligne minimale a affiché
- ``Repository`` : access à la base de donnés
- ``Service`` : service métier controlle les traitements des donnés 


survie dans le cas où la database ne fonctionne pas sur locale alors voici la commande pour fabriquer un radeau de secours
```bash
docker run --name eggland-db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=eggland_db -p 5433:5432 -d postgres
```
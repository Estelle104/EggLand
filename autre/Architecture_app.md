# Architecture Applicative — EggLand SI

> Spring Boot + Thymeleaf + Bootstrap 5 · Pattern MVC + couche Service

---

## Structure des packages

```
src/
└── main/
    ├── java/
    │   └── com/ferme/pondeuse/
    │       │
    │       ├── config/
    │       │   ├── SecurityConfig.java         ← règles Spring Security (routes, rôles)
    │       │   └── WebMvcConfig.java           ← config Thymeleaf, encodage
    │       │
    │       ├── entity/                          ← entités JPA (@Entity)
    │       │   ├── Race.java
    │       │   ├── Batiment.java
    │       │   ├── Lot.java
    │       │   ├── Mort.java
    │       │   ├── Traitement.java
    │       │   ├── OeufProduction.java
    │       │   ├── OeufStatut.java
    │       │   ├── Nourriture.java
    │       │   ├── MvtStock.java
    │       │   ├── MvtArgent.java
    │       │   ├── Employe.java
    │       │   ├── Client.java
    │       │   ├── Vente.java
    │       │   ├── DetailVente.java
    │       │   ├── Livraison.java
    │       │   ├── UserAdmin.java
    │       │   ├── Notification.java
    │       │   └── Configuration.java
    │       │
    │       ├── repository/                     ← accès BDD (extends JpaRepository)
    │       │   ├── RaceRepository.java
    │       │   ├── BatimentRepository.java
    │       │   ├── LotRepository.java
    │       │   ├── MortRepository.java
    │       │   ├── TraitementRepository.java
    │       │   ├── OeufProductionRepository.java
    │       │   ├── OeufStatutRepository.java
    │       │   ├── NourritureRepository.java
    │       │   ├── MvtStockRepository.java
    │       │   ├── MvtArgentRepository.java
    │       │   ├── EmployeRepository.java
    │       │   ├── ClientRepository.java
    │       │   ├── VenteRepository.java
    │       │   ├── DetailVenteRepository.java
    │       │   ├── LivraisonRepository.java
    │       │   ├── UserRepository.java
    │       │   └── NotificationRepository.java
    │       │
    │       ├── service/                        ← logique métier (@Service)
    │       │   ├── RaceService.java
    │       │   ├── BatimentService.java
    │       │   ├── LotService.java
    │       │   ├── MortService.java
    │       │   ├── TraitementService.java
    │       │   ├── OeufProductionService.java
    │       │   ├── OeufStatutService.java
    │       │   ├── OeufService.java            ← méthode partagée getStockDisponible()
    │       │   ├── NourritureService.java
    │       │   ├── MvtStockService.java
    │       │   ├── MvtArgentService.java       ← service central finance
    │       │   ├── EmployeService.java
    │       │   ├── PaiementSalaireService.java
    │       │   ├── ClientService.java
    │       │   ├── VenteService.java
    │       │   ├── LivraisonService.java
    │       │   ├── FinanceService.java
    │       │   ├── DashboardService.java
    │       │   ├── NotificationService.java
    │       │   ├── PdfExportService.java       ← iText 7
    │       │   ├── ExcelExportService.java     ← Apache POI
    │       │   └── UserDetailsServiceImpl.java ← Spring Security
    │       │
    │       ├── controller/                     ← requêtes HTTP (@Controller)
    │       │   ├── RaceController.java
    │       │   ├── BatimentController.java
    │       │   ├── LotController.java
    │       │   ├── MortController.java
    │       │   ├── TraitementController.java
    │       │   ├── OeufController.java
    │       │   ├── SakafoController.java
    │       │   ├── EmployeController.java
    │       │   ├── VenteController.java
    │       │   ├── LivraisonController.java
    │       │   ├── ClientController.java
    │       │   ├── ClientAuthController.java   ← inscription/login côté client
    │       │   ├── UserAdminController.java
    │       │   ├── FinanceController.java
    │       │   ├── DashboardController.java
    │       │   └── ClientSpaceController.java  ← espace client (commandes, profil)
    │       │
    │       └── FermePondeuseApplication.java   ← point d'entrée @SpringBootApplication
    │
    └── resources/
        ├── application.properties              ← BDD, port, Thymeleaf config
        ├── static/
        │   ├── css/
        │   │   └── style.css                   ← styles custom (en plus de Bootstrap)
        │   └── js/
        │       └── vente-form.js               ← formulaire multi-lignes dynamique (Izaia)
        │
        └── templates/
            ├── layout.html                     ← layout principal (navbar + sidebar)
            ├── dashboard.html                  ← page d'accueil admin
            │
            ├── fragments/
            │   └── notifications.html          ← dropdown notifications navbar
            │
            ├── races/
            │   ├── liste.html
            │   └── form.html
            │
            ├── batiments/
            │   ├── liste.html
            │   └── form.html
            │
            ├── lots/
            │   ├── liste.html
            │   ├── form.html
            │   └── detail.html                 ← onglets Bootstrap (prod, mort, traitements)
            │
            ├── mortalite/
            │   └── historique.html
            │
            ├── traitements/
            │   └── historique.html
            │
            ├── oeufs/
            │   ├── saisie.html
            │   └── stats.html
            │
            ├── stock/
            │   ├── liste.html
            │   ├── entree.html
            │   ├── sortie.html
            │   └── historique.html
            │
            ├── employes/
            │   ├── liste.html
            │   ├── form.html
            │   ├── historique.html
            │   └── recap.html
            │
            ├── ventes/
            │   ├── liste.html
            │   ├── form.html
            │   └── detail.html
            │
            ├── livraisons/
            │   ├── liste.html
            │   └── form.html
            │
            ├── finance/
            │   └── index.html
            │
            ├── admin/
            │   ├── users.html
            │   └── clients.html
            │
            ├── login.html                      ← page login admin
            │
            └── client/                         ← espace client séparé
                ├── layout.html                 ← layout client (différent de l'admin)
                ├── inscription.html
                ├── commandes.html
                ├── livraisons.html
                └── profil.html
```

---

## Les 4 couches — rôle de chacune

| Couche | Annotation | Rôle | Exemple |
|--------|-----------|------|---------|
| **Model** | `@Entity` | Représente une table BDD | `Lot.java` |
| **Repository** | `@Repository` | Parle à la BDD (SELECT, INSERT...) | `LotRepository.java` |
| **Service** | `@Service` | Contient la logique métier | `LotService.java` |
| **Controller** | `@Controller` | Reçoit les requêtes HTTP, appelle le Service, retourne un template | `LotController.java` |

---

## Conventions équipe (à respecter par tous)

| Règle | Exemple correct | Exemple incorrect |
|-------|----------------|-------------------|
| Noms de classes en PascalCase | `LotService` | `lotservice` |
| Noms de méthodes en camelCase | `getStockDisponible()` | `GetStockDisponible()` |
| Un Controller par module | `VenteController.java` | Tout dans `MainController.java` |
| Le Controller n'accède jamais au Repository directement | Controller → Service → Repository | Controller → Repository ❌ |
| Chaque méthode Service fait une seule chose | `calculerTaux()`, `enregistrerMort()` | `faireToutes()` ❌ |
| Templates dans le bon dossier | `templates/lots/liste.html` | `templates/liste_lots.html` ❌ |
| Branches Git par membre | `feature/toky-auth` | Travailler sur `main` ❌ |

---

## Flux d'une requête HTTP (exemple : afficher la liste des lots)

```
Navigateur
    │
    │  GET /lots
    ▼
LotController.java
    │  appelle
    ▼
LotService.java
    │  appelle
    ▼
LotRepository.java
    │  exécute
    ▼
Base de données MySQL
    │  retourne List<Lot>
    ▼
LotService.java  →  LotController.java
    │  ajoute au Model Thymeleaf
    ▼
templates/lots/liste.html
    │  rendu HTML
    ▼
Navigateur (page affichée)
```
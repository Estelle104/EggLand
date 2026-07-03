# Module Bâtiment

## 1. Critères de validation

| # | Fonctionnalité | Critère de validation |
|---|----------------|------------------------|
| V1 | Créer un bâtiment | Une nouvelle ligne apparaît dans `batiment` et le bâtiment est visible dans la liste. |
| V2 | Modifier un bâtiment | La ligne du bâtiment est mise à jour avec les nouvelles valeurs. |
| V3 | Supprimer un bâtiment | Le bâtiment est supprimé, sauf s'il est lié à des lots : dans ce cas un message d'erreur s'affiche. |
| V4 | Afficher la liste | La page liste affiche tous les bâtiments avec le bon titre de page. |

---

## 2. Règles de gestion

- **RG1 — Le nom d'un bâtiment est obligatoire.**
- **RG2 — La capacité d'un bâtiment doit être cohérente pour accueillir des lots.**
- **RG3 — Un bâtiment lié à des lots ne peut pas être supprimé tant que les dépendances existent.**

---

## 3. Règles d'organisation

- **RO1 — Séparation Controller / Service / Repository.**
	- Le `Controller` reçoit la requête HTTP, appelle le service et redirige.
	- Le `Service` encapsule les opérations métier du module.
	- Le `Repository` ne fait que l'accès aux données.

- **RO2 — Les erreurs métier s'affichent dans la vue.** Le controller utilise `RedirectAttributes` avec le flash attribute `error` quand la suppression est refusée.

- **RO3 — Une vue = un seul rôle.**
	- `liste.html` : afficher les bâtiments.
	- `form.html` : créer ou modifier un bâtiment.

- **RO4 — Routes du module :**
	- `GET  /batiments` -> afficher la liste des bâtiments
	- `GET  /batiments/nouveau` -> afficher le formulaire de création
	- `POST /batiments/save` -> enregistrer un bâtiment
	- `GET  /batiments/edit/{id}` -> afficher le formulaire de modification
	- `GET  /batiments/delete/{id}` -> supprimer un bâtiment
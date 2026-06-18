Bonjour 👋
Si tu veux éviter d’écrire des requêtes SQL/JPQL dans ton `Repository` avec `@Query`, **Spring Data JPA permet de générer automatiquement les requêtes à partir du nom des méthodes**.

Ça s’appelle les **Derived Query Methods**.

Exemple :

### Entité

```java
@Entity
public class Utilisateur {

    @Id
    @GeneratedValue
    private Long id;

    private String nom;
    private Integer age;
    private Boolean actif;

}
```

### Repository

```java
@Repository
public interface UtilisateurRepository
        extends JpaRepository<Utilisateur, Long> {

}
```

---

## 1. WHERE simple

Au lieu de :

```java
@Query("SELECT u FROM Utilisateur u WHERE u.nom=:nom")
List<Utilisateur> rechercher(@Param("nom") String nom);
```

Tu fais :

```java
List<Utilisateur> findByNom(String nom);
```

Utilisation :

```java
repository.findByNom("Jean");
```

---

## 2. Plusieurs conditions (`AND`)

```java
List<Utilisateur> findByNomAndAge(
        String nom,
        Integer age
);
```

SQL généré :

```sql
WHERE nom=? AND age=?
```

---

## 3. OU (`OR`)

```java
List<Utilisateur> findByNomOrAge(
        String nom,
        Integer age
);
```

---

## 4. Comparaison (`>`, `<`, `>=`, `<=`)

```java
findByAgeGreaterThan(Integer age);

findByAgeLessThan(Integer age);

findByAgeGreaterThanEqual(Integer age);

findByAgeLessThanEqual(Integer age);
```

Exemple :

```java
repository.findByAgeGreaterThan(18);
```

---

## 5. LIKE

Commence par :

```java
findByNomStartingWith(String nom);
```

Finit par :

```java
findByNomEndingWith(String nom);
```

Contient :

```java
findByNomContaining(String nom);
```

Exemple :

```java
findByNomContaining("est");
```

SQL :

```sql
WHERE nom LIKE '%est%'
```

---

## 6. Vérifier existence

```java
boolean existsByNom(String nom);
```

Exemple :

```java
if(repository.existsByNom("Jean"))
```

---

## 7. Compter

```java
long countByActif(Boolean actif);
```

---

## 8. Trier

```java
List<Utilisateur>
findByAgeGreaterThanOrderByNomAsc(
    Integer age
);
```

ou :

```java
repository.findAll(
    Sort.by("nom")
);
```

---

## 9. Pagination

```java
Page<Utilisateur>
findByActif(
    Boolean actif,
    Pageable pageable
);
```

Utilisation :

```java
PageRequest.of(0,10);
```

---

## 10. Cas plus complexes → `Specification`

Quand ça devient dynamique :

```java
public interface UserRepo extends
JpaRepository<User, Long>,
JpaSpecificationExecutor<User> {
}
```

Puis :

```java
Specification<User> spec =
(root, query, cb) ->
cb.equal(
    root.get("actif"),
    true
);

repo.findAll(spec);
```

---

En général :

* **CRUD simple → méthodes déjà générées (`findBy...`)**
* **Conditions moyennes → Derived Query**
* **Conditions dynamiques → Specification**
* **Très complexe → `@Query`**

Pour un projet Spring Boot de gestion classique, tu peux faire **80–90 % sans écrire une seule requête SQL**.

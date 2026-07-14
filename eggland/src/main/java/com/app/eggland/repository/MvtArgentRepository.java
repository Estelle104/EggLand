package com.app.eggland.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.MvtArgent;

@Repository
public interface MvtArgentRepository extends JpaRepository<MvtArgent, Integer> {
    List<MvtArgent> findAllByOrderByDateDesc();

    Optional<MvtArgent> findByReference(String reference);

    void deleteByReference(String reference);

    @Query("SELECT DISTINCT m.categorie FROM MvtArgent m WHERE m.categorie IS NOT NULL ORDER BY m.categorie")
    List<String> findDistinctCategories();

    @Query("SELECT SUM(m.montant) FROM MvtArgent m WHERE LOWER(m.type.code) = LOWER(:typeCode)")
    BigDecimal sumMontantByTypeCode(@Param("typeCode") String typeCode);

    @Query("SELECT SUM(m.montant) FROM MvtArgent m WHERE LOWER(m.type.code) = LOWER(:typeCode) AND m.date BETWEEN :debut AND :fin")
    BigDecimal sumMontantByTypeCodeBetweenDates(@Param("typeCode") String typeCode, @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    @Query("SELECT m.categorie, SUM(m.montant) FROM MvtArgent m WHERE LOWER(m.type.code) = LOWER(:typeCode) GROUP BY m.categorie ORDER BY m.categorie")
    List<Object[]> sumMontantByCategorie(@Param("typeCode") String typeCode);

    @Query("SELECT m.categorie, SUM(m.montant) FROM MvtArgent m WHERE LOWER(m.type.code) = LOWER(:typeCode) AND m.date BETWEEN :debut AND :fin GROUP BY m.categorie ORDER BY m.categorie")
    List<Object[]> sumMontantByCategorieBetweenDates(@Param("typeCode") String typeCode,
            @Param("debut") LocalDate debut, @Param("fin") LocalDate fin);

    @Query("SELECT m.lot.id, SUM(m.montant) FROM MvtArgent m WHERE LOWER(m.type.code) = 'sortie' AND m.lot IS NOT NULL GROUP BY m.lot.id")
    List<Object[]> sumMontantByLotForSorties();

    @Query(value = """
            SELECT TO_CHAR(m.date, 'YYYY-MM') AS mois, COALESCE(SUM(m.montant), 0)
            FROM mvtargent m
            JOIN typemvt t ON m.id_type = t.id
            WHERE LOWER(t.code) = LOWER(:typeCode) AND m.date BETWEEN :debut AND :fin
            GROUP BY TO_CHAR(m.date, 'YYYY-MM')
            ORDER BY TO_CHAR(m.date, 'YYYY-MM')
            """, nativeQuery = true)
    List<Object[]> sumMontantByMoisBetweenDates(@Param("typeCode") String typeCode, @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(m.montant), 0) FROM MvtArgent m WHERE LOWER(m.type.code) = 'sortie' AND m.categorie = :cat AND m.date BETWEEN :debut AND :fin")
    BigDecimal sumDepensesByCategorieBetweenDates(@Param("cat") String cat, @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin);
}

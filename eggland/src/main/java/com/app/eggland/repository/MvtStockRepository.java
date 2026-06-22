package com.app.eggland.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.app.eggland.model.MvtStock;
import com.app.eggland.model.TypeMvt;

@Repository
public interface MvtStockRepository extends JpaRepository<MvtStock, Integer> {
    
    //si SUM(m.quantite) est null return 0 pour eviter que ca plante
    @Query("SELECT COALESCE(SUM(m.quantite), 0) FROM MvtStock m WHERE m.nourriture.id = :nourritureId AND m.type = :type")
    BigDecimal sumQuantiteByNourritureAndType(@Param("nourritureId") Integer nourritureId, @Param("type") TypeMvt type);
}

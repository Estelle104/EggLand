-- ============================================================
-- RESET DES DONNÉES - EggLand
-- ============================================================
-- Script de réinitialisation complète des données
-- sans dropper la base ni les tables.
-- ============================================================
-- Usage :
--   psql -U postgres -d eggland_db -f reset.sql
-- ============================================================

-- Supprime toutes les données et réinitialise les séquences
TRUNCATE TABLE
    VersementSalaire, PaiementSalaire, Livraison, DetailVente, Vente,
    Client, MvtArgent, MvtStock, Nourriture, OeufStatut, OeufProduction,
    Traitement, Reforme, Mort, Lot, Batiment, Race, Employe, UserAdmin,
    Configuration, RoleUser, StatutLivraison, ProduitVente, StatutVente,
    StatutClient, TypeMvt, StatutOeuf, TypeTraitement, StatutLot
RESTART IDENTITY CASCADE;

package com.app.eggland.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.eggland.model.Client;
import com.app.eggland.model.DetailVente;
import com.app.eggland.model.Lot;
import com.app.eggland.model.LotRace;
import com.app.eggland.model.ProduitVente;
import com.app.eggland.model.Race;
import com.app.eggland.model.StatutVente;
import com.app.eggland.model.Vente;
import com.app.eggland.repository.DetailVenteRepository;
import com.app.eggland.repository.LotRaceRepository;
import com.app.eggland.repository.ProduitVenteRepository;
import com.app.eggland.repository.StatutVenteRepository;
import com.app.eggland.repository.VenteRepository;

@Service
public class VenteService {

    public static final String CODE_PRODUIT_POULE = "poule";

    @Autowired private VenteRepository        venteRepository;
    @Autowired private DetailVenteRepository  detailVenteRepository;
    @Autowired private ProduitVenteRepository produitVenteRepository;
    @Autowired private StatutVenteRepository  statutVenteRepository;
    @Autowired private LotRaceRepository      lotRaceRepository;
    @Autowired private MvtArgentService       mvtArgentService;
    @Autowired private OeufService            oeufService;
    @Autowired private LotService             lotService;

    public void saveVente(Vente vente) { venteRepository.save(vente); }

    public List<Vente> listeVente() { return venteRepository.findAll(); }

    public List<StatutVente> listeStatutVente() { return statutVenteRepository.findAll(); }

    public List<Vente> filtrerVentes(Integer clientId, Integer statutId,
                                      LocalDate dateDebut, LocalDate dateFin) {
        boolean hasClient = clientId != null;
        boolean hasStatut = statutId != null;
        boolean hasDates  = dateDebut != null || dateFin != null;
        LocalDate debut = dateDebut != null ? dateDebut : LocalDate.of(2000, 1, 1);
        LocalDate fin   = dateFin   != null ? dateFin   : LocalDate.of(2100, 12, 31);

        if (hasClient && hasStatut && hasDates)
            return venteRepository.findByClientIdAndStatutIdAndDateBetweenOrderByDateDesc(clientId, statutId, debut, fin);
        if (hasClient && hasStatut)
            return venteRepository.findByClientIdAndStatutIdOrderByDateDesc(clientId, statutId);
        if (hasClient && hasDates)
            return venteRepository.findByClientIdAndDateBetweenOrderByDateDesc(clientId, debut, fin);
        if (hasStatut && hasDates)
            return venteRepository.findByStatutIdAndDateBetweenOrderByDateDesc(statutId, debut, fin);
        if (hasClient)
            return venteRepository.findByClientIdOrderByDateDesc(clientId);
        if (hasStatut)
            return venteRepository.findByStatutIdOrderByDateDesc(statutId);
        if (hasDates)
            return venteRepository.findByDateBetweenOrderByDateDesc(debut, fin);
        return venteRepository.findAllByOrderByDateDesc();
    }

    public void supprimerVente(int id) {
        List<DetailVente> details = detailVenteRepository.findByVenteId(id);
        // On restitue les poules au(x) lot(s) concernés avant de supprimer la vente
        for (DetailVente d : details) {
            restituerPouleSiApplicable(d);
        }
        detailVenteRepository.deleteAll(details);
        venteRepository.deleteById(id);
    }

    public Vente trouverVenteParId(int id) {
        return venteRepository.findById(id).orElse(null);
    }

    public List<ProduitVente> listeProduitVente() { return produitVenteRepository.findAll(); }

    public ProduitVente trouverProduitVenteParId(int id) {
        return produitVenteRepository.findById(id).orElse(null);
    }

    public ProduitVente trouverProduitVenteParCode(String code) {
        return produitVenteRepository.findByCode(code).orElse(null);
    }

    public StatutVente trouverStatutVenteParCode(String code) {
        return statutVenteRepository.findByCode(code).orElse(null);
    }

    public List<DetailVente> listeDetailVente(int idVente) {
        return detailVenteRepository.findByVenteId(idVente);
    }

    private boolean estPoule(ProduitVente produit) {
        return produit != null && CODE_PRODUIT_POULE.equalsIgnoreCase(produit.getCode());
    }

    /**
     * Vérifie le stock disponible et diminue lot_races.nombre pour le
     * couple (lot, race) concerné. Ne modifie JAMAIS le statut du lot.
     */
    private void decrementerNombrePoule(Integer lotId, Integer raceId, BigDecimal quantite) {
        if (lotId == null || raceId == null) {
            throw new RuntimeException("Le lot et la race sont obligatoires pour une vente de poule.");
        }
        LotRace lotRace = lotRaceRepository.findByLotIdAndRaceId(lotId, raceId);
        if (lotRace == null) {
            throw new RuntimeException(
                "Aucune race id=" + raceId + " trouvée pour le lot id=" + lotId);
        }
        int nombreActuel = lotRace.getNombre() != null ? lotRace.getNombre() : 0;
        int demande = quantite.intValue();
        if (demande > nombreActuel) {
            throw new RuntimeException(
                "Stock de poules insuffisant pour le lot " + lotId
                + " / race " + raceId + ". Disponible : " + nombreActuel
                + " | Demandé : " + demande);
        }
        lotRace.setNombre(nombreActuel - demande);
        lotRaceRepository.save(lotRace);
    }

    /** Remet dans lot_races.nombre la quantité d'une ligne de vente "poule" (ex: suppression/modification). */
    private void restituerPouleSiApplicable(DetailVente d) {
        if (!estPoule(d.getProduit())) return;
        if (d.getLot() == null || d.getRace() == null) return;

        LotRace lotRace = lotRaceRepository.findByLotIdAndRaceId(d.getLot().getId(), d.getRace().getId());
        if (lotRace == null) return; // rien à restituer si la ligne n'a plus de correspondance

        int nombreActuel = lotRace.getNombre() != null ? lotRace.getNombre() : 0;
        lotRace.setNombre(nombreActuel + d.getQuantite().intValue());
        lotRaceRepository.save(lotRace);
    }

    @Transactional
    public void enregistrerVente(int clientId,
                                  List<Integer> produitIds,
                                  List<Integer> lotIds,
                                  List<Integer> raceIds,
                                  List<BigDecimal> quantites,
                                  List<BigDecimal> prixUnitaires,
                                  Client client) {

        BigDecimal total = BigDecimal.ZERO;

        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;
            BigDecimal qte  = quantites.get(i);
            BigDecimal prix = prixUnitaires.get(i);
            total = total.add(qte.multiply(prix));

            if ("oeuf".equalsIgnoreCase(produit.getCode())) {
                int stockDispo = oeufService.getStockDisponible();
                if (qte.intValue() > stockDispo) {
                    throw new RuntimeException(
                        "Stock d'œufs insuffisant. Disponible : " + stockDispo
                        + " | Demandé : " + qte.intValue());
                }
                oeufService.retirerDuStock(qte.intValue());
            }

            if (estPoule(produit)) {
                Integer lotId  = (lotIds  != null && i < lotIds.size())  ? lotIds.get(i)  : null;
                Integer raceId = (raceIds != null && i < raceIds.size()) ? raceIds.get(i) : null;
                // On vérifie le stock dès maintenant pour échouer tôt si besoin.
                decrementerNombrePoule(lotId, raceId, qte);
            }
        }

        StatutVente statut = statutVenteRepository.findByCode("paye")
            .orElseThrow(() -> new RuntimeException("Statut 'paye' introuvable en base"));

        Vente vente = Vente.builder()
            .client(client).date(LocalDate.now()).total(total).statut(statut).build();
        venteRepository.save(vente);

        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;

            DetailVente.DetailVenteBuilder builder = DetailVente.builder()
                .vente(vente).client(client).produit(produit)
                .quantite(quantites.get(i)).prixUnitaire(prixUnitaires.get(i));

            if (estPoule(produit)) {
                Integer lotId  = (lotIds  != null && i < lotIds.size())  ? lotIds.get(i)  : null;
                Integer raceId = (raceIds != null && i < raceIds.size()) ? raceIds.get(i) : null;
                if (lotId != null) builder.lot(Lot.builder().id(lotId).build());
                if (raceId != null) builder.race(Race.builder().id(raceId).build());
            }

            detailVenteRepository.save(builder.build());
            // NOTE : on ne réforme plus le lot ici (le statut du lot reste inchangé).
            // Seul lot_races.nombre est diminué, via decrementerNombrePoule ci-dessus.
        }

        mvtArgentService.creerEntree(total, LocalDate.now(), "vente");
    }

    @Transactional
    public void enregistrerModificationVente(int venteId,
                                              List<Integer> produitIds,
                                              List<Integer> lotIds,
                                              List<Integer> raceIds,
                                              List<BigDecimal> quantites,
                                              List<BigDecimal> prixUnitaires,
                                              Client client) {

        Vente vente = venteRepository.findById(venteId)
            .orElseThrow(() -> new RuntimeException("Vente introuvable"));

        List<DetailVente> anciensDetails = detailVenteRepository.findByVenteId(venteId);

        int ancienneQteOeuf = 0;
        for (DetailVente d : anciensDetails) {
            if ("oeuf".equalsIgnoreCase(d.getProduit().getCode()))
                ancienneQteOeuf += d.getQuantite().intValue();
        }

        int nouvelleQteOeuf = 0;
        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente p = trouverProduitVenteParId(produitIds.get(i));
            if (p == null) continue;
            BigDecimal qte = quantites.get(i);
            total = total.add(qte.multiply(prixUnitaires.get(i)));
            if ("oeuf".equalsIgnoreCase(p.getCode())) nouvelleQteOeuf += qte.intValue();
        }

        int diff = nouvelleQteOeuf - ancienneQteOeuf;
        if (diff > 0) {
            int stock = oeufService.getStockDisponible();
            if (diff > stock) throw new RuntimeException(
                "Stock insuffisant pour ajouter " + diff + " œufs. Disponible : " + stock);
            oeufService.retirerDuStock(diff);
        } else if (diff < 0) {
            oeufService.ajouterAuStock(Math.abs(diff));
        }

        // 1) On restitue les poules des anciennes lignes (remet le stock lot_races à jour)
        for (DetailVente d : anciensDetails) {
            restituerPouleSiApplicable(d);
        }

        detailVenteRepository.deleteAll(anciensDetails);

        // 2) On vérifie et décrémente le stock pour les nouvelles lignes "poule"
        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;
            if (estPoule(produit)) {
                Integer lotId  = (lotIds  != null && i < lotIds.size())  ? lotIds.get(i)  : null;
                Integer raceId = (raceIds != null && i < raceIds.size()) ? raceIds.get(i) : null;
                decrementerNombrePoule(lotId, raceId, quantites.get(i));
            }
        }

        // 3) On recrée les lignes de détail
        for (int i = 0; i < produitIds.size(); i++) {
            ProduitVente produit = trouverProduitVenteParId(produitIds.get(i));
            if (produit == null) continue;

            DetailVente.DetailVenteBuilder builder = DetailVente.builder()
                .vente(vente).client(client).produit(produit)
                .quantite(quantites.get(i)).prixUnitaire(prixUnitaires.get(i));

            if (estPoule(produit)) {
                Integer lotId  = (lotIds  != null && i < lotIds.size())  ? lotIds.get(i)  : null;
                Integer raceId = (raceIds != null && i < raceIds.size()) ? raceIds.get(i) : null;
                if (lotId != null) builder.lot(Lot.builder().id(lotId).build());
                if (raceId != null) builder.race(Race.builder().id(raceId).build());
            }

            detailVenteRepository.save(builder.build());
        }

        vente.setClient(client);
        vente.setTotal(total);
        venteRepository.save(vente);
        mvtArgentService.creerEntree(total, LocalDate.now(), "Modification Vente #" + vente.getId());
    }
}

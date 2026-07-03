package com.app.eggland.service;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.Employe;
import com.app.eggland.repository.EmployeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeService {

    private final EmployeRepository employeRepository;

    public List<Employe> listerTous() {
        return employeRepository.findAllByOrderByNomAscPrenomAsc();
    }

    public Employe trouverParId(Integer id) {
        return employeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable : " + id));
    }

    public Employe creer(Employe employe) {
        employe.setId(null);
        verifierDateEmbauche(employe.getDateEmbauche());
        return employeRepository.save(employe);
    }

    public Employe modifier(Integer id, String nom, String prenom, String tel, BigDecimal salaire, LocalDate dateEmbauche) {
        verifierDateEmbauche(dateEmbauche);
        Employe employe = trouverParId(id);
        employe.setNom(nom);
        employe.setPrenom(prenom);
        employe.setTel(tel);
        employe.setSalaire(salaire);
        employe.setDateEmbauche(dateEmbauche);
        return employeRepository.save(employe);
    }

    public void supprimer(Integer id) {
        employeRepository.deleteById(id);
    }

    /** Règle de gestion : la date d'embauche ne peut pas être dans le futur. */
    private void verifierDateEmbauche(LocalDate dateEmbauche) {
        if (dateEmbauche == null) {
            throw new IllegalArgumentException("La date d'embauche est obligatoire.");
        }
        if (dateEmbauche.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date d'embauche ne peut pas être dans le futur.");
        }
    }

    /*méthode pour la pagination*/
    public List<Employe> getPage(List<Employe> employes, int page, int size) {
        int start = page * size;
        int end = Math.min(start + size, employes.size());
        if (start > end) {
            return List.of(); // Retourne une liste vide si la page demandée est hors limites
        }
        return employes.subList(start, end);
    }
}

package com.app.eggland.service;

import com.app.eggland.model.Employe;
import com.app.eggland.repository.EmployeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        return employeRepository.save(employe);
    }

    public Employe modifier(Integer id, String nom, String prenom, String tel, BigDecimal salaire) {
        Employe employe = trouverParId(id);
        employe.setNom(nom);
        employe.setPrenom(prenom);
        employe.setTel(tel);
        employe.setSalaire(salaire);
        return employeRepository.save(employe);
    }

    public void supprimer(Integer id) {
        employeRepository.deleteById(id);
    }
}

package com.app.eggland.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.eggland.model.TypeTraitement;
import com.app.eggland.repository.TypeTraitementRepository;

@Service
public class TypeTraitementService {
    @Autowired
    private TypeTraitementRepository typeTraitementRepository;

    public List<TypeTraitement> findAll() {
        return typeTraitementRepository.findAll();
    }
    
    public Optional<TypeTraitement> findById(Integer id) {
        return typeTraitementRepository.findById(id);
    }
    
    public Optional<TypeTraitement> findByCode(String code) {
        return typeTraitementRepository.findByCode(code);
    }
    
    public TypeTraitement save(TypeTraitement typeTraitement) {
        return typeTraitementRepository.save(typeTraitement);
    }
    
    public void deleteById(Integer id) {
        typeTraitementRepository.deleteById(id);
    }
}

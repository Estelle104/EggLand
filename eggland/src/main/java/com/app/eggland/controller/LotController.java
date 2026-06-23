package com.app.eggland.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.Lot;
import com.app.eggland.model.StatutLot;
import com.app.eggland.repository.BatimentRepository;
import com.app.eggland.repository.RaceRepository;
import com.app.eggland.repository.StatutLotRepository;

import com.app.eggland.service.LotService;

import jakarta.transaction.Transactional;

@RequestMapping("/lots")
@Controller
public class LotController {
    @Autowired
    LotService lotService;
    @Autowired
    private RaceRepository raceRepository;
    
    @Autowired
    private BatimentRepository batimentRepository;
  
    @Autowired
    private StatutLotRepository statutLotRepository;

     @GetMapping("/create")
    public ModelAndView showLotForm(){
        ModelAndView mav = new ModelAndView("lots/form");
        mav.addObject("lot", new Lot());
        mav.addObject("races", raceRepository.findAll());
        mav.addObject("batiments", batimentRepository.findAll());
        return mav;
    }
      @PostMapping("/create")
    public ModelAndView createLot(@ModelAttribute Lot lot){
        ModelAndView mav = new ModelAndView();
        
        try {
          
            if (lot == null) {
                throw new IllegalArgumentException(" Lot inexistant");
            }
            
            if (lot.getDateArrivee() == null) {
                throw new IllegalArgumentException(" La date d'arrivée est vide");
            }
            
            if (lot.getBatiment() == null || lot.getBatiment().getId() == null) {
                throw new IllegalArgumentException(" Le bâtiment est inexistant");
            }
            
            if (lot.getRace() == null) {
                throw new IllegalArgumentException(" La race est inexistante");
            }
            
            if (lot.getNombreInitial() <= 0) {
                throw new IllegalArgumentException(" Le nombre initial doit être positif");
            }
            
          
            lotService.createLot(lot);
            
            
            mav.setViewName("redirect:/lots/list");
            
        } catch (IllegalArgumentException e) {
           
            System.out.println("Erreur d'insertion: " + e.getMessage());
            
            mav.setViewName("lots/form");
            mav.addObject("lot", lot);
            mav.addObject("error", e.getMessage());
            mav.addObject("races", raceRepository.findAll());
            mav.addObject("batiments", batimentRepository.findAll());
        }
        
        return mav;
    }
  

@GetMapping("/list")
public ModelAndView showAllLot(
        @RequestParam(required = false) Integer batiment,
        @RequestParam(required = false) Integer statut) {

    ModelAndView mav = new ModelAndView("lots/liste");

    List<Lot> lots;

    if (batiment != null && statut != null) {

        Batiment b = batimentRepository.findById(batiment).orElse(null);
        StatutLot s = statutLotRepository.findById(statut).orElse(null);

        lots = lotService.findByBatimentAndStatut(b, s);

    } else if (batiment != null && statut == null) {

        Batiment b = batimentRepository.findById(batiment).orElse(null);

                lots = lotService.findByBatimentOrStatut(b, null);


    } else if (statut != null && batiment == null) {

        StatutLot s = statutLotRepository.findById(statut).orElse(null);

                       lots = lotService.findByBatimentOrStatut(null, s);
;

    } else {

        lots = lotService.getAllLots();
    }

    mav.addObject("lots", lots);
    mav.addObject("batiments", batimentRepository.findAll());
    mav.addObject("statuts", statutLotRepository.findAll());

    return mav;
}


}

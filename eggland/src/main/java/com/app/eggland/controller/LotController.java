package com.app.eggland.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.eggland.model.Batiment;
import com.app.eggland.model.Lot;
import com.app.eggland.model.Race;
import com.app.eggland.model.StatutLot;
import com.app.eggland.repository.BatimentRepository;
import com.app.eggland.repository.RaceRepository;
import com.app.eggland.repository.StatutLotRepository;

import com.app.eggland.service.LotService;

import jakarta.transaction.Transactional;

@RequestMapping("/lots")
@RestController
public class LotController {
    @Autowired
    LotService lotService;
    @Autowired
    private RaceRepository raceRepository;
    
    @Autowired
    private BatimentRepository batimentRepository;
  
    @Autowired
    private StatutLotRepository statutLotRepository;



     @GetMapping("/data/races")
public List<Race> getRaces() {
    return raceRepository.findAll();
}
    @GetMapping("/data/batiments")
    public List<Batiment> getBatiments() {
        return batimentRepository.findAll();
    }

    @GetMapping("/data/statuts")
    public List<StatutLot> getStatuts() {
        return statutLotRepository.findAll();
    }
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
    @GetMapping("/detail/{id}")
    public ModelAndView detail(@PathVariable Integer id) {

        ModelAndView mav = new ModelAndView("lots/detail");
        mav.addObject("lot", lotService.getDetailLot(id));

        return mav;
    }
@PostMapping("/reforme/{idLot}")  
public ModelAndView reformerLot(
    @PathVariable Integer idLot,
    @RequestParam(required = false) LocalDate dateReform,
RedirectAttributes redirectAttributes) {
    
    ModelAndView mav = new ModelAndView();
    
    try {
      
        System.out.println("idLot: " + idLot);
        System.out.println("dateReform: " + dateReform);
        
     
        if (dateReform == null) {
            throw new IllegalArgumentException("Date de réforme requise");
        }
        
        if (dateReform.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La date de réforme ne peut pas être dans le futur");
        }

        Lot lot = lotService.findById(idLot);
        
        if (lot.getStatut().getCode().equals("reforme")) {
            throw new IllegalArgumentException("Lot déja reformé");
        }
        
        System.out.println("Validation OK - appel lotService.reformerUnLot()");
        
     
        lotService.reformerUnLot(idLot, dateReform); 
        
       
        
    

redirectAttributes.addFlashAttribute("success", "Reforme effectuée avec succès");
mav.setViewName("redirect:/lots/list");
        return mav;
        

    } catch (Exception e) {
       
        System.out.println(" Exception inattendue: " + e.getClass().getName());
        e.printStackTrace();
        
        mav.setViewName("lots/liste");
        mav.addObject("error", "Erreur inattendue: " + e.getMessage());
        mav.addObject("idLot", idLot);
        mav.addObject("dateReform", dateReform);
    
        List<Lot> lots = lotService.getAllLots();
        mav.addObject("lots", lots != null ? lots : new ArrayList<>());
        
        return mav;
    }
}

@PostMapping("/modifier/{id}")
public ModelAndView modifierLots(@PathVariable("id") Integer id,
                                  @ModelAttribute Lot formLot) {
    ModelAndView mav = new ModelAndView("lots/liste");
    Lot lot = lotService.findById(id);
try{

    
    

    
    if (formLot.getRace() != lot.getRace()) {
        lot.setRace(formLot.getRace());
    }

    if (formLot.getStatut() != lot.getStatut()) {
        lot.setStatut(formLot.getStatut());
    }
    
    if (formLot.getBatiment() != lot.getBatiment()) {
        lot.setBatiment(formLot.getBatiment());
    }
    
    if (formLot.getNombreInitial() != lot.getNombreInitial()) {
        lot.setNombreInitial(formLot.getNombreInitial());
    }
    
    lotService.updateLot(lot);
} catch (IllegalArgumentException e) {
           
            System.out.println("Erreur d'insertion: " + e.getMessage());
            
            mav.setViewName("lots/liste");
            mav.addObject("lot", lot);
            mav.addObject("error", e.getMessage());
            mav.addObject("races", raceRepository.findAll());
            mav.addObject("batiments", batimentRepository.findAll());
        }

   return mav;
}

@GetMapping("/supprimer/{id}")
public ModelAndView deleteLot(@PathVariable Integer id) {

    Lot lot = lotService.findById(id);

    if (lot == null) {
        throw new IllegalArgumentException("Lot introuvable");
    }

    lotService.deleteLot(lot.getId());

    return new ModelAndView("redirect:/lots/list");
}
}

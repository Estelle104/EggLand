package com.app.eggland.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.app.eggland.model.Race;
import com.app.eggland.service.RaceService;

@Controller
public class RaceController {
    @Autowired
    private RaceService raceService;

    public RaceService getRaceService() {
        return raceService;
    }

    @GetMapping("/races")
    public String listeRaces(Model model){
    List<Race> races = raceService.findAll();
    model.addAttribute("races", races);
    return "races/liste";
    }

}

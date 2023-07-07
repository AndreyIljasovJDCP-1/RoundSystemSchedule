package ru.billiard.roundSystem.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.billiard.roundSystem.models.Player;
import ru.billiard.roundSystem.services.PlayerService;

@Controller
@RequestMapping("/")
public class MainController {
    @Autowired
    PlayerService playerService;

    @GetMapping("/")
    public String start() {
        return "redirect:/about";
    }
    @GetMapping("/about")
    public String about() {
        return "about";
    }
    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("list", playerService.all());
        return "players";
    }
    @GetMapping("/search")
    public String search(@RequestParam(name = "name", required = false) String name, Model model) {
        model.addAttribute("list", playerService.search(name));
        return "players";
    }
    @GetMapping("/draw")
    public String draw(Model model) {
        model.addAttribute("list", playerService.draw());
        return "players";
    }
    @GetMapping("/select")
    public String select(Model model, @RequestParam(name = "algorithm") String algorithm) {
        playerService.selectSchedule(algorithm);
        model.addAttribute("list", playerService.all());
        return "players";
    }
    @GetMapping("/table")
    public String table(Model model) {
        model.addAttribute("list", playerService.getTable());
        model.addAttribute("gamesInTour", playerService.all().size()/2);
        return "games";
    }
    @GetMapping("/print")
    public String print(Model model) {
        playerService.printTable();
        model.addAttribute("list", playerService.getTable());
        model.addAttribute("gamesInTour", playerService.all().size()/2);
        return "games";
    }
    @GetMapping("/load")
    public String load(Model model) {
        playerService.load();
        model.addAttribute("list", playerService.all());
        return "players";
    }
    @GetMapping("/file")
    public String file(Model model) {
        playerService.save();
        model.addAttribute("list", playerService.getTable());
        model.addAttribute("gamesInTour", playerService.all().size()/2);
        return "games";
    }
    @GetMapping("/delete")
    public String delete(Model model, @RequestParam(name = "name") String name) {
        playerService.delete(name);
        model.addAttribute("list", playerService.all());
        return "players";
    }
    @PostMapping("/add")
    public String add(Model model, Player player) {
        playerService.save(player);
        model.addAttribute("list", playerService.all());
        return "players";
    }
}

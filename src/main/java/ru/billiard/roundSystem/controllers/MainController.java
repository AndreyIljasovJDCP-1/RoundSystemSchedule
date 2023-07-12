package ru.billiard.roundSystem.controllers;


import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.billiard.roundSystem.models.Player;
import ru.billiard.roundSystem.services.PlayerService;

@Controller
@RequestMapping("/")
@AllArgsConstructor
public class MainController {
    private final PlayerService playerService;

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
        playerService.draw();
        model.addAttribute("list", playerService.all());
        return "players";
    }
    @GetMapping("/select")
    public String select(Model model, @RequestParam(name = "algorithm") String algorithm) {
        playerService.setAlgorithmSchedule(algorithm);
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
        model.addAttribute("gamesInTour", playerService.all().size() / 2);
        return "games";
    }
    /*@GetMapping("/delete") // удаляет всех по совпадению имени
    public String delete(Model model, @RequestParam(name = "name") String name) {
        playerService.delete(name);
        model.addAttribute("list", playerService.all());
        return "players";
    }*/
    @PostMapping("/deletePost") // Удалить игрока по совпадению имени и рейтинга
    public String deletePost(Model model, Player player) {
        playerService.deletePost(player);
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

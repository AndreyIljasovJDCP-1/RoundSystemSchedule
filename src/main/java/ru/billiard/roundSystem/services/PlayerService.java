package ru.billiard.roundSystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.billiard.roundSystem.FileHandler;
import ru.billiard.roundSystem.Schedule;
import ru.billiard.roundSystem.models.Player;
import ru.billiard.roundSystem.repositories.PlayerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    @Qualifier("simpleAlgorithm")
    Schedule schedule;
    @Autowired
    FileHandler fileHandler;

    public void selectSchedule(String algorithm) {
        schedule = "simple".equals(algorithm)
                ? applicationContext.getBean(SimpleAlgorithm.class)
                : applicationContext.getBean(HomeGuestAlgorithm.class);
    }

    public List<Player> all() {
        return playerRepository.all();
    }

    public void save(Player player) {
        playerRepository.save(player);
    }

    public List<Player> draw() {
        if (playerRepository.all().size() % 2 != 0) {
            playerRepository.save(new Player("пропуск"));
        }
        return playerRepository.draw();
    }

    public List<Player> search(String name) {
        return name.isEmpty()
                ? playerRepository.all()
                : playerRepository.all()
                .stream()
                .filter(s -> s.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void load() {
        playerRepository.saveAll(fileHandler.read());
    }

    public void save() {
        var playerList = playerRepository.all();
        int gamesInTour = playerList.size() / 2;
        var listSchedule = schedule.getSchedule(playerList);
        fileHandler.write(listSchedule, gamesInTour);
    }

    public List<List<Player>> getTable() {
        if (playerRepository.all().size() % 2 != 0) {
            playerRepository.save(new Player("пропуск"));
        }
        return schedule.getSchedule(playerRepository.all());
    }

    public void printTable() {
        schedule.printSchedule(playerRepository.all());
    }

    public void delete(String name) {
        playerRepository.delete(name);
    }
}

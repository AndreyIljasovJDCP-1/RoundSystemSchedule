package ru.billiard.roundSystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.billiard.roundSystem.ExcelHandler;
import ru.billiard.roundSystem.Schedule;
import ru.billiard.roundSystem.models.Player;
import ru.billiard.roundSystem.repositories.PlayerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    Schedule schedule = new SimpleAlgorithm();
    @Autowired
    ExcelHandler excelHandler;

    public void selectSchedule(String algorithm) {
        schedule = "simple".equals(algorithm)
                ? new SimpleAlgorithm()
                : new HomeGuestAlgorithm();
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
        playerRepository.saveAll(excelHandler.load());
    }

    public void write() {
        int gamesInTour = playerRepository.all().size() / 2;
        var listSchedule = schedule.getScheduleAsList(playerRepository.all());
        excelHandler.write(listSchedule, gamesInTour);
    }

    public List<List<Player>> getTableAsList() {
        if (playerRepository.all().size() % 2 != 0) {
            playerRepository.save(new Player("пропуск"));
        }
        return schedule.getScheduleAsList(playerRepository.all());
    }

    public void printTable() {
        schedule.printSchedule(playerRepository.all());
    }
}

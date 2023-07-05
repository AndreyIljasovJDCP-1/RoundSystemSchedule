package ru.billiard.roundSystem.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.billiard.roundSystem.models.Player;
import ru.billiard.roundSystem.repositories.PlayerRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    @Autowired
    PlayerRepository playerRepository;
    @Autowired
    Schedule schedule;

    public List<Player> all() {
        return playerRepository.all();
    }

    public void save(Player player) {
        playerRepository.save(player);
    }

    public List<Player> draw() {
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
        schedule.load();
    }

    public void write(String algorithm) {
        schedule.write(algorithm);
    }

    public void printTable(String algorithm) {
        if ("simple".equals(algorithm)) {
            schedule.printSimpleTable();
        } else {
            schedule.printTable();
        }
    }

    public List<List<Player>> getTableAsList(String algorithm) {
        return "simple".equals(algorithm)
                ? schedule.getSimpleTableAsList()
                : schedule.getTableAsList();
    }
}

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

    public List<Player> save(Player player) {
        return playerRepository.save(player);
    }

    public List<Player> draw() {
        return playerRepository.draw();
    }
    public List<Player> search(String name) {
        return name.isEmpty()
                ? playerRepository.all()
                : playerRepository.all()
                .stream()
                .filter(s->s.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void load() {
        schedule.load();
    }
    public void write() {
        schedule.write();
    }
    public void printTable() {
        schedule.printTable();
    }

    public List<List<Player>> getTableAsList() {
        return schedule.getTableAsList();
    }
}

package ru.billiard.roundSystem;

import ru.billiard.roundSystem.models.Player;

import java.util.List;

public interface FileHandler {
    List<Player> read();
    void write(List<List<Player>> schedule, int gamesInTour);
}

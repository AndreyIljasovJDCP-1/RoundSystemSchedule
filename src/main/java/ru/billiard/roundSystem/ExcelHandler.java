package ru.billiard.roundSystem;

import ru.billiard.roundSystem.models.Player;

import java.util.List;

public interface ExcelHandler {
    List<Player> load();
    void write(List<List<Player>> schedule, int gamesInTour);
}

package ru.billiard.roundSystem;

import ru.billiard.roundSystem.models.Player;

import java.util.List;

public interface Schedule {

    List<List<Player>> getSchedule(List<Player> playerList);
    void printSchedule(List<Player> playerList);
}

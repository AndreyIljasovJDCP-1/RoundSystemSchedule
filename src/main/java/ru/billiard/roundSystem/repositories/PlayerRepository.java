package ru.billiard.roundSystem.repositories;

import org.springframework.stereotype.Repository;
import ru.billiard.roundSystem.models.Player;

import java.util.*;

@Repository
public class PlayerRepository {
    private final Random random = new Random();
    private final List<Player> playerList;

    public PlayerRepository() {
        this.playerList = new ArrayList<>();
    }

    public List<Player> all() {
        return playerList;
    }

    public List<Player> draw() {
        if (playerList.size() % 2 != 0) {
            playerList.add(new Player("пропуск",0));
        }
        Set<Integer> drawSet = new HashSet<>();
        for (Player player : playerList) {
            boolean flag = false;
            do {
                int choice = random.nextInt(playerList.size()) + 1;
                if (drawSet.add(choice)) {
                    player.setDraw(choice);
                    flag = true;
                }
            } while (!flag);
        }
        playerList.sort(Comparator.comparingInt(Player::getDraw));
        return playerList;
    }

    public void save(Player player) {
        if (!playerList.contains(player)) {
            playerList.add(player);
            playerList.sort(null);
        }
    }
}

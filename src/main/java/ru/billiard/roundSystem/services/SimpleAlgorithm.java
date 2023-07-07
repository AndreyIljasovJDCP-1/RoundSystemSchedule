package ru.billiard.roundSystem.services;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.billiard.roundSystem.Schedule;
import ru.billiard.roundSystem.models.Player;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Component
@NoArgsConstructor
public class SimpleAlgorithm implements Schedule {


    /**
     * 1 всегда дома, простая схема
     * Составление расписания турнира по круговой системе 1 круг,
     * без учета домашних и гостевых встреч.
     * Сделано с помощью 2-х мерного массива и Deque.
     * Алгоритм поворот по часовой стрелке, см.
     * <a href="https://youtu.be/MLf67p2Darc">
     * круговая система</a>
     */
    @Override
    public List<List<Player>> getSchedule(List<Player> playerList) {
        int players = playerList.size();
        int tours = players - 1;
        int gamesInTour = players / 2;
        int[][] gameTable = new int[gamesInTour][2];
        Deque<Integer> deque = new ArrayDeque<>();
        List<List<Player>> schedule = new ArrayList<>();
        // заполним очередь
        for (int i = 2; i <= players; i++) {
            deque.offerFirst(i);
        }
        gameTable[0][0] = 1;
        for (int j = 0; j < tours; j++) {

            for (int i = 0; i < gamesInTour; i++) {
                int next = deque.pollFirst();
                gameTable[i][1] = next;
                deque.offerLast(next);
            }
            for (int i = gamesInTour - 1; i > 0; i--) {
                int next = deque.pollFirst();
                gameTable[i][0] = next;
                deque.offerLast(next);
            }

            for (int i = 0; i < gamesInTour; i++) {
                int home = gameTable[i][0];
                int guest = gameTable[i][1];
                schedule.add(List.of(
                        playerList.get(home - 1),
                        playerList.get(guest - 1)
                ));
            }
            deque.offerFirst(deque.pollLast());
        }
        return schedule;
    }

    @Override
    public void printSchedule(List<Player> playerList) {
        var scheduleAsList = getSchedule(playerList);
        int players = playerList.size();
        int tours = players - 1;
        int gamesInTour = players / 2;
        int count = 0;
        for (int i = 0; i < tours; i++) {
            System.out.printf("       %d  Тур.%n", i + 1);
            for (int j = 0; j < gamesInTour; j++) {
                System.out.printf("%d. %s - %s%n", j + 1,
                        scheduleAsList.get(count).get(0),
                        scheduleAsList.get(count++).get(1));

            }
        }
    }
}

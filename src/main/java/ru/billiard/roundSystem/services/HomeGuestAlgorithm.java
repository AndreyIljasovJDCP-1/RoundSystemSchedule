package ru.billiard.roundSystem.services;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;
import ru.billiard.roundSystem.Schedule;
import ru.billiard.roundSystem.models.Player;

import java.util.ArrayList;
import java.util.List;
@Component
@NoArgsConstructor
public class HomeGuestAlgorithm implements Schedule {

    /**
     * Формирование листа пар игроков по всем играм
     *
     * @return List пар игроков по играм
     */
    @Override
    public List<List<Player>> getSchedule(List<Player> playerList) {
        int[][][] gameTable = generateTable(playerList);
        List<List<Player>> schedule = new ArrayList<>();
        int players = playerList.size();
        int tours = players - 1;
        int gamesInTour = players / 2;
        for (int i = 0; i < tours; i++) {

            for (int j = 0; j < gamesInTour; j++) {
                int home = gameTable[i][j][0];
                int guest = gameTable[i][j][1];
                schedule.add(List.of(
                        playerList.get(home - 1),
                        playerList.get(guest - 1)
                ));
            }
        }
        return schedule;
    }

    @Override
    public void printSchedule(List<Player> playerList) {
        int[][][] gameTable = generateTable(playerList);
        int players = playerList.size();
        int tours = players - 1;
        int gamesInTour = players / 2;
        for (int i = 0; i < tours; i++) {
            System.out.printf("       %d  Тур.%n", i + 1);
            for (int j = 0; j < gamesInTour; j++) {
                int home = gameTable[i][j][0];
                int guest = gameTable[i][j][1];
                System.out.printf("%d. %s - %s%n", j + 1,
                        playerList.get(home - 1),
                        playerList.get(guest - 1));
            }
        }
    }

    /**
     * Составление расписания турнира по круговой системе 1 круг,
     * с учетом домашних и гостевых встреч.
     * Сделано с помощью 3-х мерного массива.
     * Алгоритм заполнение пустых граф в таблице,
     * см. <a href="http://chess.sainfo.ru/tabl/tablei.html">
     * как провести круговой турнир
     * </a>
     */
    private int[][][] generateTable(List<Player> playerList) {
        int players = playerList.size();
        int tours = players - 1;
        int gamesInTour = players / 2;
        int[][][] gameTable = new int[tours][gamesInTour][2];

        //1 этап: записываем последнего.
        for (int i = 0; i < tours; i++) {
            gameTable[i][0][(i + 1) % 2] = players;
        }
        // 2 этап заполняем 1234567 итд по строкам
        int count = 1;
        for (int i = 0; i < tours; i++) {
            for (int j = 0; j < gamesInTour; j++) {
                if (gameTable[i][j][0] == 0) {
                    gameTable[i][j][0] = count;
                } else {
                    gameTable[i][j][1] = count;
                }
                count++;
                count = count > tours ? 1 : count;
            }
        }
        //третий этап заполняем оставшиеся места 7654321 по строкам
        count = tours;
        for (int i = 0; i < tours; i++) {
            for (int j = 0; j < gamesInTour; j++) {
                if (gameTable[i][j][0] == 0) {
                    gameTable[i][j][0] = count;
                    count--;
                } else if (gameTable[i][j][1] == 0) {
                    gameTable[i][j][1] = count;
                    count--;
                } else {
                    continue;
                }
                count = count > 0 ? count : tours;
            }
        }
        return gameTable;
    }
}

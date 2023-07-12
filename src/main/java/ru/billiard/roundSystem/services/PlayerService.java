package ru.billiard.roundSystem.services;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.billiard.roundSystem.FileHandler;
import ru.billiard.roundSystem.Schedule;
import ru.billiard.roundSystem.models.Player;
import ru.billiard.roundSystem.repositories.JpaPlayerRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class PlayerService {
    private static final Sort RATE_SORT = Sort.by(Sort.Direction.DESC, "rate")
            .and(Sort.by(Sort.Direction.ASC, "name"));
    private static final Sort DRAW_SORT = Sort.by(Sort.Direction.ASC, "draw");
    private static final ExampleMatcher MATCHER = ExampleMatcher
            .matching()
            .withIgnorePaths("id", "draw");
    private static final Random RANDOM = new Random();
    private final JpaPlayerRepository playerRepository;
    private final ApplicationContext applicationContext;
    private Schedule schedule;
    private final FileHandler fileHandler;
    private boolean ranked = false;

    //IoC, 1 конструктор @Autowired не обязателен,
    public PlayerService(
            JpaPlayerRepository playerRepository,
            ApplicationContext applicationContext,
            @Qualifier("simple") Schedule schedule,
            FileHandler fileHandler
    ) {
        this.playerRepository = playerRepository;
        this.applicationContext = applicationContext;
        this.schedule = schedule;
        this.fileHandler = fileHandler;
    }

    public void setAlgorithmSchedule(String algorithm) {
        schedule = applicationContext.getBean(algorithm, Schedule.class);
    }

    public List<Player> all() {
        //return playerRepository.findAllByOrderByRateDescNameAsc();
        return playerRepository.findAll(ranked ? DRAW_SORT : RATE_SORT);
    }

    /*@Transactional
    public void save(Player player) {
        if (!playerRepository.existsByName(player.getName())) {
            playerRepository.deleteByName("пропуск");
            playerRepository.save(player);
            ranked = false;
        }
    }*/
    @Transactional
    public void save(Player player) {
        var example = Example.of(player, MATCHER);
        if (!playerRepository.exists(example)) {
            playerRepository.deleteByName("пропуск");
            playerRepository.save(player);
            ranked = false;
        }
    }

    public void draw() {
        checkPlayerList();
        var playerList = playerRepository.findAll();
        Set<Integer> drawSet = new HashSet<>();
        for (Player player : playerList) {
            boolean flag = false;
            do {
                int choice = RANDOM.nextInt(playerList.size()) + 1;
                if (drawSet.add(choice)) {
                    player.setDraw(choice);
                    flag = true;
                }
            } while (!flag);
        }
        playerRepository.deleteAll();
        playerRepository.saveAll(playerList);
        ranked = true;
    }

    public List<Player> search(String name) {
        return name.isEmpty()
                ? playerRepository.findAll(ranked ? DRAW_SORT : RATE_SORT)
                : playerRepository.findByNameContaining(name).orElseThrow();
    }

    public void load() {
        playerRepository.saveAll(fileHandler.read());
    }

    public void save() {
        var playerList = playerRepository.findAll(ranked ? DRAW_SORT : RATE_SORT);
        int gamesInTour = playerList.size() / 2;
        var listSchedule = schedule.getSchedule(playerList);
        fileHandler.write(listSchedule, gamesInTour);
    }

    public List<List<Player>> getTable() {
        checkPlayerList();
        return schedule.getSchedule(playerRepository.findAll(ranked ? DRAW_SORT : RATE_SORT));
    }

    public void printTable() {
        schedule.printSchedule(playerRepository.findAll(ranked ? DRAW_SORT : RATE_SORT));
    }

    /*@Transactional
    public void delete(String name) {
        if (playerRepository.existsByName(name)) {
            playerRepository.deleteByName("пропуск");
            playerRepository.deleteByName(name);
            ranked = false;
        }
    }*/
    // Удалить по ID, если совпадает имя и рейтинг
    //todo  сделать поле статус - пометка на удаление
    @Transactional
    public void deletePost(Player player) {
        var example = Example.of(player, MATCHER);
        if (playerRepository.exists(example)) {
            var id =
                    playerRepository.findOne(example).orElseThrow().getId();
            playerRepository.deleteById(id);
            playerRepository.deleteByName("пропуск");
            ranked = false;
        }
    }

    private void checkPlayerList() {
        if (playerRepository.findAll().size() % 2 != 0) {
            playerRepository.save(new Player("пропуск"));
            ranked = false;
        }
    }
}

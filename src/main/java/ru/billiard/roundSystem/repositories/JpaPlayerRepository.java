package ru.billiard.roundSystem.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.billiard.roundSystem.models.Player;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaPlayerRepository extends JpaRepository<Player, Long> {

    Optional<List<Player>> findByNameContaining(String name);

    //List<Player> findAllByOrderByRateDescNameAsc();
    boolean existsByName(String name);

    void deleteByName(String name);
}

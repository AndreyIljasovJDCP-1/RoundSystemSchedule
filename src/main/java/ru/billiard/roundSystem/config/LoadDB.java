package ru.billiard.roundSystem.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.billiard.roundSystem.FileHandler;
import ru.billiard.roundSystem.repositories.JpaPlayerRepository;

@Configuration
public class LoadDB {

    @Bean
    @ConditionalOnProperty(name = "listPlayers.file.load", havingValue = "true", matchIfMissing = true)
    CommandLineRunner initDatabase(JpaPlayerRepository repository, FileHandler excelHandler) {

        return args -> repository.saveAll(excelHandler.read());
    }
}

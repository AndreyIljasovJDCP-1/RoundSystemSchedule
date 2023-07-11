package ru.billiard.roundSystem.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Entity
@NoArgsConstructor
public class Player implements Comparable<Player> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private int rate;
    private int draw;

    public Player(String name, int rate) {
        this.name = name;
        this.rate = rate;
    }

    public Player(String name) {
        this(name,0);
    }
    @Override
    public String toString() {
        return name + "(" + rate + ")[" + draw + "]";
    }

    /**
     * Реализация compareTo сравнение в порядке убывания по рейтингу,
     * а после по имени по возрастанию.
     *
     * @param o the object to be compared.
     * @return -1 ,0,1
     */
    @Override
    public int compareTo(Player o) {
        if (rate > o.rate) {
            return -1;
        } else if (rate < o.rate) {
            return 1;
        } else {
            return name.compareTo(o.name);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        if (rate != player.rate) return false;
        return name.equals(player.name);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + rate;
        return result;
    }
}

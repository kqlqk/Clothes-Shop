package me.kqlqk.shop.repository;

import me.kqlqk.shop.model.Color;
import me.kqlqk.shop.model.enums.Colors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {
    Optional<Color> findByName(Colors name);

    boolean existsByName(Colors name);
}

package me.kqlqk.shop.repository;

import me.kqlqk.shop.model.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SizeRepository extends JpaRepository<Size, Long> {
    Optional<Size> findByName(String name);

    boolean existsByName(String name);
}

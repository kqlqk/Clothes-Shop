package me.kqlqk.shop.repository;

import me.kqlqk.shop.model.Tag;
import me.kqlqk.shop.model.Tags;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(Tags name);

    boolean existsByName(Tags name);
}

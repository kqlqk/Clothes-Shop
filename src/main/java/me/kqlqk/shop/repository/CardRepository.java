package me.kqlqk.shop.repository;

import me.kqlqk.shop.model.Card;
import me.kqlqk.shop.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByUser(User user);

    boolean existsByUser(User user);
}

package me.kqlqk.shop.repository;

import me.kqlqk.shop.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken findByUserEmail(String userEmail);

    boolean existsByToken(String token);
}

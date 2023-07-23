package me.kqlqk.shop.repository;

import me.kqlqk.shop.model.user.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserEmail(String userEmail);

    boolean existsByUserEmail(String email);

    boolean existsByToken(String token);
}

package me.kqlqk.shop.service;

import me.kqlqk.shop.model.user.RefreshToken;
import org.springframework.stereotype.Service;

@Service
public interface RefreshTokenService {
    RefreshToken getByUserEmail(String email);

    void add(RefreshToken refreshToken);

    void update(RefreshToken refreshToken);

    boolean existsByUserEmail(String email);
}

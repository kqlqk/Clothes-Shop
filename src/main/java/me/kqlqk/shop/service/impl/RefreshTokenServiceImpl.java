package me.kqlqk.shop.service.impl;

import lombok.NonNull;
import me.kqlqk.shop.exception.RefreshTokenExistsException;
import me.kqlqk.shop.exception.RefreshTokenNotFoundException;
import me.kqlqk.shop.model.RefreshToken;
import me.kqlqk.shop.repository.RefreshTokenRepository;
import me.kqlqk.shop.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public RefreshTokenServiceImpl(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public RefreshToken getByUserEmail(String email) {
        return refreshTokenRepository.findByUserEmail(email).orElseThrow(
                () -> new RefreshTokenNotFoundException("Refresh token with email = " + email + " exists"));
    }

    @Override
    public void add(@NonNull RefreshToken refreshToken) {
        if (refreshTokenRepository.existsById(refreshToken.getId())) {
            throw new RefreshTokenExistsException("Refresh token with id = " + refreshToken.getId() + " exists");
        }
        if (refreshTokenRepository.existsByToken(refreshToken.getToken())) {
            throw new RefreshTokenExistsException("Refresh token with token = " + refreshToken.getToken() + " exists");
        }

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public void update(@NonNull RefreshToken refreshToken) {
        if (!refreshTokenRepository.existsById(refreshToken.getId())) {
            throw new RefreshTokenNotFoundException("Refresh token with id = " + refreshToken.getId() + " not found");
        }

        refreshTokenRepository.save(refreshToken);
    }

    @Override
    public boolean existsByUserEmail(String email) {
        return refreshTokenRepository.existsByUserEmail(email);
    }
}

package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.RefreshTokenExistsException;
import me.kqlqk.shop.exception.RefreshTokenNotFoundException;
import me.kqlqk.shop.model.user.RefreshToken;
import me.kqlqk.shop.repository.RefreshTokenRepository;
import me.kqlqk.shop.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class RefreshTokenServiceImplIT {
    @Autowired
    private RefreshTokenServiceImpl refreshTokenService;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserService userService;

    @Test
    public void add_shouldAddRefreshTokenToDB() {
        int oldSize = refreshTokenRepository.findAll().size();

        RefreshToken token = new RefreshToken(userService.getById(2), "token");

        refreshTokenService.add(token);

        int newSize = refreshTokenRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        RefreshToken refreshToken = refreshTokenService.getByUserEmail("email@email.com");
        refreshToken.setToken("new token");

        RefreshToken finalRefreshToken1 = refreshToken;
        assertThrows(RefreshTokenExistsException.class, () -> refreshTokenService.add(finalRefreshToken1));

        refreshToken = new RefreshToken(userService.getById(2), "token1");
        RefreshToken finalRefreshToken2 = refreshToken;
        assertThrows(RefreshTokenExistsException.class, () -> refreshTokenService.add(finalRefreshToken2));
    }

    @Test
    public void update_shouldUpdateUserInDB() {
        RefreshToken refreshToken = refreshTokenService.getByUserEmail("email@email.com");
        refreshToken.setToken("new token");

        refreshTokenService.update(refreshToken);

        assertThat(refreshTokenService.getByUserEmail("email@email.com").getToken()).isEqualTo(refreshToken.getToken());
    }

    @Test
    public void update_shouldThrowException() {
        RefreshToken refreshToken = new RefreshToken(userService.getById(1), "token");
        assertThrows(RefreshTokenNotFoundException.class, () -> refreshTokenService.update(refreshToken));
    }
}

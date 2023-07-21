package me.kqlqk.shop.util;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.TokenException;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.service.RefreshTokenService;
import me.kqlqk.shop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class JwtUtilIT {
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private UserService userService;

    @Test
    public void generateAccessToken_shouldGenerateAccessToken() {
        String token = jwtUtil.generateAccessToken("email@email.com");

        assertThat(token).matches("(^[\\w-]*\\.[\\w-]*\\.[\\w-]*$)");
        assertThat(jwtUtil.validateAccessToken(token)).isTrue();
    }

    @Test
    public void generateAccessToken_shouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> jwtUtil.generateAccessToken("badEmail"));
    }

    @Test
    public void generateAndSaveOrUpdateRefreshToken_shouldGenerateAndSaveOrUpdateRefreshToken() {
        String email = "email2@email.com";

        assertThat(refreshTokenService.existsByUserEmail(email)).isFalse();

        String token = jwtUtil.generateAndSaveOrUpdateRefreshToken(email);

        assertThat(token).matches("(^[\\w-]*\\.[\\w-]*\\.[\\w-]*$)");
        assertThat(refreshTokenService.existsByUserEmail(email)).isTrue();
    }

    @Test
    public void generateAndSaveOrUpdateRefreshToken_shouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> jwtUtil.generateAndSaveOrUpdateRefreshToken("badEmail"));
    }

    @Test
    public void updateRefreshTokenWithNewEmail_shouldUpdateRefreshTokenWithNewEmail() {
        String email = "email@email.com";
        String newEmail = "newEmail@mail.com";
        String oldToken = refreshTokenService.getByUserEmail(email).getToken();

        User user = userService.getByEmail(email);
        user.setEmail(newEmail);
        userService.update(user);

        String token = jwtUtil.updateRefreshTokenWithNewEmail(newEmail);

        assertThat(token).matches("(^[\\w-]*\\.[\\w-]*\\.[\\w-]*$)");
        assertThat(oldToken).isNotEqualTo(token);
    }

    @Test
    public void updateRefreshTokenWithNewEmail_shouldThrowException() {
        assertThrows(UserNotFoundException.class, () -> jwtUtil.updateRefreshTokenWithNewEmail("badEmail"));
    }

    @Test
    public void accessTokenErrorChecking_shouldDoesNotThrowException() {
        assertDoesNotThrow(() -> jwtUtil.accessTokenErrorChecking(jwtUtil.generateAccessToken("email@email.com")));
    }

    @Test
    public void accessTokenErrorChecking_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtUtil.accessTokenErrorChecking("badtoken"));
    }

    @Test
    public void refreshTokenErrorChecking_shouldDoesNotThrowException() {
        assertDoesNotThrow(() -> jwtUtil.refreshRefreshTokenErrorChecking(jwtUtil.generateAndSaveOrUpdateRefreshToken("email@email.com")));
    }

    @Test
    public void refreshTokenErrorChecking_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtUtil.refreshRefreshTokenErrorChecking("badtoken"));
    }

    @Test
    public void validateAccessToken_shouldValidateAccessToken() {
        assertThat(jwtUtil.validateAccessToken(jwtUtil.generateAccessToken("email@email.com"))).isTrue();
    }

    @Test
    public void validateAccessToken_shouldThrowException() {
        assertThat(jwtUtil.validateAccessToken("badtoken")).isFalse();
    }

    @Test
    public void validateRefreshToken_shouldValidateRefreshToken() {
        assertThat(jwtUtil.validateRefreshToken(jwtUtil.generateAndSaveOrUpdateRefreshToken("email@email.com"))).isTrue();
    }

    @Test
    public void validateRefreshToken_ShouldThrowException() {
        assertThat(jwtUtil.validateRefreshToken("badtoken")).isFalse();
    }

    @Test
    public void getEmailFromAccessToken_shouldReturnEmail() {
        String email = jwtUtil.getEmailFromAccessToken(jwtUtil.generateAccessToken("email@email.com"));

        assertThat(email).matches("^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,}$");
    }

    @Test
    public void getEmailFromAccessToken_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtUtil.getEmailFromAccessToken("badtoken"));
    }

    @Test
    public void getEmailFromRefreshToken_shouldReturnEmail() {
        String email = jwtUtil.getEmailFromRefreshToken(jwtUtil.generateAndSaveOrUpdateRefreshToken("email@email.com"));

        assertThat(email).matches("^[^\\s@]{3,}@[^\\s@]{2,}\\.[^\\s@]{2,}$");
    }

    @Test
    public void getEmailFromRefreshToken_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtUtil.getEmailFromRefreshToken("badtoken"));
    }

    @Test
    public void getIdFromAccessToken_shouldReturnId() {
        long id = jwtUtil.getIdFromAccessToken(jwtUtil.generateAccessToken("email@email.com"));

        assertThat(id).isEqualTo(1);
    }

    @Test
    public void getIdFromAccessToken_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtUtil.getIdFromAccessToken("badtoken"));
    }

    @Test
    public void getIdFromRefreshToken_shouldReturnId() {
        long id = jwtUtil.getIdFromRefreshToken(jwtUtil.generateAndSaveOrUpdateRefreshToken("email@email.com"));

        assertThat(id).isEqualTo(1);
    }

    @Test
    public void getIdFromRefreshToken_shouldThrowException() {
        assertThrows(TokenException.class, () -> jwtUtil.getIdFromRefreshToken("badtoken"));
    }
}

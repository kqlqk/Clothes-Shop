package me.kqlqk.shop.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import me.kqlqk.shop.exception.TokenException;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.model.RefreshToken;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.service.RefreshTokenService;
import me.kqlqk.shop.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtUtil {
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final Key accessKey;
    private final Key refreshKey;

    public JwtUtil(UserService userService,
                   RefreshTokenService refreshTokenService,
                   @Value("${jwt.access.secret}") String accessSecret,
                   @Value("${jwt.refresh.secret}") String refreshSecret) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
        refreshKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(refreshSecret));
    }

    public String generateAccessToken(@NonNull String email) {
        if (!userService.existsByEmail(email)) {
            throw new UserNotFoundException("User with email = " + email + " not found");
        }

        Date expiresIn = Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expiresIn)
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateAndSaveOrUpdateRefreshToken(@NonNull String email) {
        if (!userService.existsByEmail(email)) {
            throw new UserNotFoundException("User with email = " + email + " not found");
        }

        User user = userService.getByEmail(email);

        Date expiresIn = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        String token = Jwts.builder()
                .setSubject(email)
                .setExpiration(expiresIn)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshToken;
        if (!refreshTokenService.existsByUserEmail(email)) {
            refreshToken = new RefreshToken(user, token);
            refreshTokenService.add(refreshToken);
        } else {
            refreshToken = refreshTokenService.getByUserEmail(email);
            refreshToken.setToken(token);
            refreshTokenService.update(refreshToken);
        }

        return token;
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken);
    }

    private boolean validateToken(@NonNull String token) {
        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if (!userService.existsByEmail(email)) {
                throw new UserNotFoundException("User with email = " + email + " not found");
            }

            return true;
        } catch (ExpiredJwtException expEx) {
            throw new TokenException("Token expired");
        } catch (UnsupportedJwtException unsEx) {
            throw new TokenException("Token unsupported");
        } catch (MalformedJwtException mjEx) {
            throw new TokenException("Token Malformed");
        } catch (SignatureException sEx) {
            throw new TokenException("Signature invalid");
        } catch (Exception e) {
            throw new TokenException("Token invalid");
        }
    }

    public String getEmailFromAccessToken(String token) {
        return getEmailFromToken(token);
    }

    public String getEmailFromRefreshToken(String token) {
        return getEmailFromToken(token);
    }

    private String getEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException expEx) {
            return expEx.getClaims().getSubject();
        } catch (UnsupportedJwtException unsEx) {
            throw new TokenException("Token unsupported");
        } catch (MalformedJwtException mjEx) {
            throw new TokenException("Token Malformed");
        } catch (SignatureException sEx) {
            throw new TokenException("Signature invalid");
        } catch (Exception e) {
            throw new TokenException("Token invalid");
        }
    }
}

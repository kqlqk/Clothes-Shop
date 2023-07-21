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

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("id", userService.getByEmail(email).getId());

        return Jwts.builder()
                .setClaims(claims)
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

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("id", user.getId());

        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiresIn)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshToken;
        if (!refreshTokenService.existsByUserEmail(email)) {
            refreshToken = new RefreshToken(user, token);
            refreshTokenService.add(refreshToken);
        }
        else {
            refreshToken = refreshTokenService.getByUserEmail(email);
            refreshToken.setToken(token);
            refreshTokenService.update(refreshToken);
        }

        return token;
    }

    public String updateRefreshTokenWithNewEmail(@NonNull String newEmail) {
        if (!userService.existsByEmail(newEmail)) {
            throw new UserNotFoundException("User with email = " + newEmail + " not found");
        }

        User user = userService.getByEmail(newEmail);

        Date expiresIn = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        Claims claims = Jwts.claims().setSubject(newEmail);
        claims.put("id", user.getId());

        String token = Jwts.builder()
                .setClaims(claims)
                .setExpiration(expiresIn)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();

        RefreshToken refreshToken = refreshTokenService.getByUserEmail(newEmail);
        refreshToken.setToken(token);
        refreshTokenService.update(refreshToken);

        return token;
    }

    public void accessTokenErrorChecking(@NonNull String accessToken) {
        tokenErrorChecking(accessToken, accessKey);
    }

    public void refreshRefreshTokenErrorChecking(@NonNull String refreshToken) {
        tokenErrorChecking(refreshToken, refreshKey);
    }

    private void tokenErrorChecking(@NonNull String token, @NonNull Key key) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            if (!userService.existsByEmail(claims.getSubject())) {
                throw new UserNotFoundException("User with email = " + claims.getSubject() + " not found");
            }

            if (!userService.existsById(((Integer) claims.get("id")).longValue())) {
                throw new UserNotFoundException("User with id = " + ((Integer) claims.get("id")).longValue() + " not found");
            }
        }
        catch (ExpiredJwtException expEx) {
            throw new TokenException("Token expired");
        }
        catch (UnsupportedJwtException unsEx) {
            throw new TokenException("Token unsupported");
        }
        catch (MalformedJwtException mjEx) {
            throw new TokenException("Token Malformed");
        }
        catch (SignatureException sEx) {
            throw new TokenException("Signature invalid");
        }
        catch (Exception e) {
            throw new TokenException("Token invalid"); // TODO: 09/07/2023 throw nested ex
        }
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, accessKey);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, refreshKey);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key key) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return userService.existsByEmail(claims.getSubject()) &&
                    userService.existsById(((Integer) claims.get("id")).longValue());
        }
        catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromAccessToken(String token) {
        return getEmailFromToken(token, accessKey);
    }

    public String getEmailFromRefreshToken(String token) {
        return getEmailFromToken(token, refreshKey);
    }

    private String getEmailFromToken(@NonNull String token, @NonNull Key key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        }
        catch (ExpiredJwtException expEx) {
            return expEx.getClaims().getSubject();
        }
        catch (UnsupportedJwtException unsEx) {
            throw new TokenException("Token unsupported");
        }
        catch (MalformedJwtException mjEx) {
            throw new TokenException("Token Malformed");
        }
        catch (SignatureException sEx) {
            throw new TokenException("Signature invalid");
        }
        catch (Exception e) {
            throw new TokenException("Token invalid");
        }
    }

    public long getIdFromAccessToken(String token) {
        return getIdFromToken(token, accessKey);
    }

    public long getIdFromRefreshToken(String token) {
        return getIdFromToken(token, refreshKey);
    }

    private long getIdFromToken(@NonNull String token, @NonNull Key key) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return ((Integer) claims.get("id")).longValue();
        }
        catch (ExpiredJwtException expEx) {
            return (long) expEx.getClaims().get("id");
        }
        catch (UnsupportedJwtException unsEx) {
            throw new TokenException("Token unsupported");
        }
        catch (MalformedJwtException mjEx) {
            throw new TokenException("Token Malformed");
        }
        catch (SignatureException sEx) {
            throw new TokenException("Signature invalid");
        }
        catch (Exception e) {
            throw new TokenException("Token invalid");
        }
    }
}

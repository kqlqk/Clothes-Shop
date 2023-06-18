package me.kqlqk.shop.util;


import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import me.kqlqk.shop.exception.UserNotFoundException;
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
    private final Key accessKey;

    public JwtUtil(UserService userService, @Value("${jwt.secret}") String accessSecret) {
        this.userService = userService;
        accessKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(accessSecret));
    }

    public String generateAccessToken(@NonNull String email) {
        if (!userService.existsByEmail(userEmail)) { // TODO implement method
            throw new UserNotFoundException("User with email = " + email + " not found");
        }

        Date expiresIn = Date.from(LocalDateTime.now().plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant());

        return Jwts.builder()
                .setSubject(email)
                .setExpiration(expiresIn)
                .signWith(accessKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken);
    }

    private boolean validateToken(@NonNull String token) {
        try {
            String email = Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();

            if (!userService.existsByEmail(email)) { // TODO implement method
                throw new UserNotFoundException("User with email = " + email + " not found");
            }

            return true;
        } catch (ExpiredJwtException expEx) {
            throw new TokenException("Token expired"); // TODO Create exception class
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

    private String getEmailFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(accessKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (ExpiredJwtException expEx) {
            throw new TokenException("Token expired"); // TODO Create exception class
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

package me.kqlqk.shop.cfg.filter.test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.cfg.filter.JwtFilter;
import me.kqlqk.shop.service.RefreshTokenService;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.JwtUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;


@Component
public class PublicAccessJwtFilter extends JwtFilter {

    public PublicAccessJwtFilter(JwtUtil jwtUtil, RefreshTokenService refreshTokenService, UserService userService) {
        super(jwtUtil, refreshTokenService, userService);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterInternal(request, response, filterChain);
    }
}

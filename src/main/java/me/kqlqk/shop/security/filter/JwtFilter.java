package me.kqlqk.shop.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import me.kqlqk.shop.exception.TokenException;
import me.kqlqk.shop.service.RefreshTokenService;
import me.kqlqk.shop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private UserDetailsService userDetailsService;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }

    @Autowired
    public void setUserDetailsService(@Lazy UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (!request.getRequestURI().startsWith("/user")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessTokenFromRequest = getToken(request);
        String email;
        email = jwtUtil.getEmailFromAccessToken(accessTokenFromRequest); // TODO: 22/06/2023 Handle exceptions

        try {
            jwtUtil.validateAccessToken(accessTokenFromRequest);
        } catch (TokenException e) {
            if (e.getMessage().equals("Token expired")) {
                if (jwtUtil.validateRefreshToken(refreshTokenService.getByUserEmail(email).getToken())) {
                    accessTokenFromRequest = jwtUtil.generateAccessToken(email);
                } else {
                    // TODO: 22/06/2023 Handle exception
                }
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(@NonNull HttpServletRequest request) {
        for (Cookie c : request.getCookies()) {
            if (c.getName().equals("accessToken")) {
                return c.getValue();
            }
        }

        return null; // TODO: 24/06/2023 Throw exception
    }
}

package me.kqlqk.shop.cfg.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import me.kqlqk.shop.exception.RefreshTokenNotFoundException;
import me.kqlqk.shop.exception.TokenException;
import me.kqlqk.shop.model.user.User;
import me.kqlqk.shop.service.RefreshTokenService;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.JwtUtil;
import me.kqlqk.shop.util.LoginErrorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private UserDetailsService userDetailsService;
    @Value("${admin.emails}")
    private String[] adminEmails;

    @Autowired
    public JwtFilter(JwtUtil jwtUtil, RefreshTokenService refreshTokenService, UserService userService) {
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
        this.userService = userService;
    }

    @Autowired
    public void setUserDetailsService(@Lazy UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if (!(requestURI.startsWith("/user") || requestURI.startsWith("/admin"))) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessTokenFromRequest;
        try {
            accessTokenFromRequest = getToken(request);
        }
        catch (TokenException e) {
            log.warn("There is no token:", e);
            response.sendRedirect("/login?error=" + LoginErrorParam.SHOULD_LOGIN);
            return;
        }

        String email;
        User user;
        try {
            email = jwtUtil.getEmailFromAccessToken(accessTokenFromRequest);
            user = userService.getByEmail(email);
        }
        catch (Exception e) {
            log.error("Error in JwtFiler from ip: " + request.getRemoteAddr(), e);
            response.sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);
            return;
        }

        if (request.getRequestURI().startsWith("/user")) {
            String[] word = requestURI.split("/");
            int id = Integer.parseInt(word[2]);

            if (user.getId() != id) {
                response.sendRedirect("redirect:/user/" + user.getId());
                return;
            }
        }

        if (request.getRequestURI().startsWith("/admin")) {
            boolean exist = false;
            for (String e : adminEmails) {
                if (e.equalsIgnoreCase(email)) {
                    exist = true;
                    break;
                }
            }

            if (!exist) {
                response.sendRedirect("redirect:/");
                return;
            }
        }

        try {
            jwtUtil.accessTokenErrorChecking(accessTokenFromRequest);
            log.info("Access token valid, token: " + accessTokenFromRequest + " User: " + email);
        }
        catch (TokenException e) {
            if (e.getMessage().equals("Token expired")) {
                String refreshToken = null;
                try {
                    refreshToken = refreshTokenService.getByUserEmail(email).getToken();

                    log.info("old token" + accessTokenFromRequest);
                    jwtUtil.refreshRefreshTokenErrorChecking(refreshToken);
                    accessTokenFromRequest = jwtUtil.generateAccessToken(email);
                    Cookie cookie = new Cookie("accessToken", accessTokenFromRequest);
                    cookie.setPath("/");
                    cookie.setMaxAge(10 * 365 * 24 * 60 * 60);
                    response.addCookie(cookie);
                    log.info("Update access token, new token: " + accessTokenFromRequest + " User: " + email);
                }
                catch (RefreshTokenNotFoundException ex) {
                    log.warn("Refresh token not found" +
                            " for user: " + email +
                            " from ip: " + request.getRemoteAddr(), ex);
                    response.sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);
                    return;
                }
                catch (TokenException ex) {
                    log.warn("Refresh token invalid, token: " + refreshToken +
                            " for user: " + email +
                            " from ip: " + request.getRemoteAddr(), ex);
                    response.sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);
                    return;
                }

            }
            else {
                log.warn("Access token invalid, token: " + accessTokenFromRequest +
                        " for user: " + email +
                        " from ip: " + request.getRemoteAddr(), e);
                response.sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);
                return;
            }
        }

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, userDetails.getAuthorities());

            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);

            log.info("Was set the auth for user: " + email);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("accessToken")) {
                    return c.getValue();
                }
            }
        }

        throw new TokenException("There is no token in cookie or cookie");
    }
}

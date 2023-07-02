package me.kqlqk.shop.cfg.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.util.JwtUtil;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(2)
public class GlobalAttributeFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Autowired
    public GlobalAttributeFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        if (request.getCookies() == null) {
            request.setAttribute("userProfileOrLoginPagePath", "/login");
            filterChain.doFilter(request, response);
            return;
        }

        for (Cookie c : request.getCookies()) {
            if (c.getName().equals("accessToken")) {
                if (jwtUtil.validateAccessToken(c.getValue())) {
                    request.setAttribute("userProfileOrLoginPagePath", "/user/" + jwtUtil.getIdFromAccessToken(c.getValue()));
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        }

        request.setAttribute("userProfileOrLoginPagePath", "/login");
        filterChain.doFilter(request, response);
    }
}

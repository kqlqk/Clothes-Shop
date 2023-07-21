package me.kqlqk.shop.cfg.filter.test;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.cfg.filter.GlobalAttributeFilter;
import me.kqlqk.shop.util.JwtUtil;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PublicGlobalAttributeFilter extends GlobalAttributeFilter {
    public PublicGlobalAttributeFilter(JwtUtil jwtUtil) {
        super(jwtUtil);
    }

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        super.doFilterInternal(request, response, filterChain);
    }
}

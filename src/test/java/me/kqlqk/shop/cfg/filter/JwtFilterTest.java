package me.kqlqk.shop.cfg.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.exception.RefreshTokenNotFoundException;
import me.kqlqk.shop.exception.TokenException;
import me.kqlqk.shop.model.RefreshToken;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.service.RefreshTokenService;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.JwtUtil;
import me.kqlqk.shop.util.LoginErrorParam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserService userService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshToken refreshToken;

    @Test
    public void doInternal_shouldGiveAccessToUserPage() throws ServletException, IOException {
        String email = "email@email.com";
        String token = "token";
        int id = 1;

        User user = new User();
        user.setId(id);
        user.setEmail(email);
        when(userService.getByEmail(email)).thenReturn(user);

        when(jwtUtil.getEmailFromAccessToken(token)).thenReturn(email);

        when(request.getRequestURI()).thenReturn("/user/" + id);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("accessToken", token)});
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        jwtFilter.setUserDetailsService(userDetailsService);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(userDetails, times(1)).getAuthorities();
    }

    @Test
    public void doInternal_doesNotMatchURI() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/main");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getRequestURI();
        verify(userDetails, times(0)).getAuthorities();
    }

    @Test
    public void doInternal_doesNotHaveToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/main");
        when(request.getRequestURI()).thenReturn("/user/1");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendRedirect("/login?error=" + LoginErrorParam.SHOULD_LOGIN);
    }

    @Test
    public void doInternal_badToken() throws ServletException, IOException {
        when(request.getRequestURI()).thenReturn("/main");
        when(request.getRequestURI()).thenReturn("/user/1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("accessToken", "token")});
        when(jwtUtil.getEmailFromAccessToken("token")).thenThrow(new RuntimeException("Exception"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);


        Mockito.reset(jwtUtil);
        when(jwtUtil.getEmailFromAccessToken("token")).thenReturn("email@email.com");
        when(userService.getByEmail("email@email.com")).thenThrow(new RuntimeException("Exception"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(2)).sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);
    }

    @Test
    public void doInternal_invalidAccessToken() throws ServletException, IOException {
        String email = "email@email.com";
        String token = "token";
        int id = 1;

        User user = new User();
        user.setId(id);
        user.setEmail(email);

        when(userService.getByEmail(email)).thenReturn(user);
        when(jwtUtil.getEmailFromAccessToken(token)).thenReturn(email);
        doThrow(new TokenException("Token expired")).when(jwtUtil).accessTokenErrorChecking(token);
        when(jwtUtil.generateAccessToken(email)).thenReturn("token2");
        when(refreshToken.getToken()).thenReturn(token);
        when(refreshTokenService.getByUserEmail(email)).thenReturn(refreshToken);
        when(request.getRequestURI()).thenReturn("/user/" + id);
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("accessToken", token)});
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        jwtFilter.setUserDetailsService(userDetailsService);
        SecurityContextHolder.clearContext();

        jwtFilter.doFilterInternal(request, response, filterChain);// test case 1

        verify(userDetails, times(1)).getAuthorities();


        reset(refreshTokenService);
        when(refreshTokenService.getByUserEmail(email)).thenThrow(new RefreshTokenNotFoundException("Exception"));

        jwtFilter.doFilterInternal(request, response, filterChain); // test case 2

        verify(response, times(1)).sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);


        reset(refreshTokenService, jwtUtil);
        when(refreshTokenService.getByUserEmail(email)).thenReturn(refreshToken);
        doThrow(new TokenException("Token expired")).when(jwtUtil).accessTokenErrorChecking(token);
        when(jwtUtil.getEmailFromAccessToken(token)).thenReturn(email);
        when(jwtUtil.generateAccessToken(email)).thenThrow(new TokenException("Exception"));

        jwtFilter.doFilterInternal(request, response, filterChain); // test case 3

        verify(response, times(2)).sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);


        reset(jwtUtil);
        when(jwtUtil.getEmailFromAccessToken(token)).thenReturn(email);
        doThrow(new TokenException("Exception")).when(jwtUtil).accessTokenErrorChecking(token);

        jwtFilter.doFilterInternal(request, response, filterChain); // test case 4

        verify(response, times(3)).sendRedirect("/login?error=" + LoginErrorParam.UNKNOWN);
    }
}

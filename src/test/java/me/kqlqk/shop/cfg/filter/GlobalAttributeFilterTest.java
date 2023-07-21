package me.kqlqk.shop.cfg.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.cfg.filter.test.PublicGlobalAttributeFilter;
import me.kqlqk.shop.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GlobalAttributeFilterTest {
    @InjectMocks
    private PublicGlobalAttributeFilter globalAttributeFilter;

    @Spy
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private JwtUtil jwtUtil;

    @Test
    public void doInternal_shouldSetAttributeForValidAccessToken() throws ServletException, IOException {
        String token = "token";

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("accessToken", token)});
        when(jwtUtil.validateAccessToken(token)).thenReturn(true);
        when(jwtUtil.getIdFromAccessToken(token)).thenReturn(1L);

        globalAttributeFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).setAttribute("userProfileOrLoginPagePath", "/user/1");
    }

    @Test
    public void doInternal_shouldSetAttributeForInvalidOrAbsenceAccessToken() throws ServletException, IOException {
        globalAttributeFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).setAttribute("userProfileOrLoginPagePath", "/login");


        String token = "token";
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("accessToken", token)});
        when(jwtUtil.validateAccessToken(token)).thenReturn(false);

        globalAttributeFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(2)).setAttribute("userProfileOrLoginPagePath", "/login");


        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("random", "cookie")});

        globalAttributeFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(3)).setAttribute("userProfileOrLoginPagePath", "/login");
    }
}

package me.kqlqk.shop.security;

import me.kqlqk.shop.exception.BadCredentialsException;
import me.kqlqk.shop.model.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomDaoAuthenticationProviderTest {
    @InjectMocks
    private CustomDaoAuthenticationProvider customDaoAuthenticationProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetails userDetails;

    @Mock
    private Authentication authentication;


    @Test
    public void authenticate_shouldAuthenticateReturnAuthToken() {
        User user = new User();
        user.setEmail("email@email.com");

        when(authentication.getPrincipal()).thenReturn(user);
        when(userDetailsService.loadUserByUsername("email@email.com")).thenReturn(userDetails);
        when(authentication.getCredentials()).thenReturn("password");
        when(passwordEncoder.matches("password", userDetails.getPassword())).thenReturn(true);

        Authentication token = customDaoAuthenticationProvider.authenticate(authentication);

        assertThat(token).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    }

    @Test
    public void authenticate_shouldThrowException() {
        User user = new User();
        user.setEmail("email@email.com");

        when(authentication.getPrincipal()).thenReturn(user);
        when(userDetailsService.loadUserByUsername("email@email.com")).thenReturn(userDetails);
        when(authentication.getCredentials()).thenReturn("password");
        when(passwordEncoder.matches("password", userDetails.getPassword())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> customDaoAuthenticationProvider.authenticate(authentication));
    }
}

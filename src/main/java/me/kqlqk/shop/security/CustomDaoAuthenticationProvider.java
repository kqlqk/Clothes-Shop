package me.kqlqk.shop.security;

import me.kqlqk.shop.exception.BadCredentialsException;
import me.kqlqk.shop.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.nio.CharBuffer;
import java.util.Arrays;

@Component
public class CustomDaoAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomDaoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        User user = (User) authentication.getPrincipal();
        char[] password = (char[]) authentication.getCredentials();

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        if (passwordEncoder.matches(CharBuffer.wrap(password), new String(user.getPassword()))) {
            Arrays.fill((char[]) authentication.getCredentials(), '0');

            return new UsernamePasswordAuthenticationToken(user, password, userDetails.getAuthorities());
        }
        else {
            Arrays.fill((char[]) authentication.getCredentials(), '0');

            throw new BadCredentialsException("Bad credentials");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}

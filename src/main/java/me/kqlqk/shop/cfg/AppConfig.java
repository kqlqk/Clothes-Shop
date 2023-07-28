package me.kqlqk.shop.cfg;

import me.kqlqk.shop.cfg.filter.JwtFilter;
import me.kqlqk.shop.model.user.User;
import me.kqlqk.shop.security.CustomDaoAuthenticationProvider;
import me.kqlqk.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@EnableWebSecurity
@Configuration
public class AppConfig {
    private final UserService userService;
    private final JwtFilter jwtFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    public AppConfig(UserService userService, JwtFilter jwtFilter, AuthenticationEntryPoint authenticationEntryPoint) {
        this.userService = userService;
        this.jwtFilter = jwtFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/user/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(new CustomDaoAuthenticationProvider(userDetailsService(), passwordEncoder()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .logout()
                .logoutSuccessUrl("/login")
                .deleteCookies("accessToken")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll();

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User userDB = userService.getByEmail(username);

            return new org.springframework.security.core.userdetails.User(
                    userDB.getEmail(),
                    userDB.getPassword(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}

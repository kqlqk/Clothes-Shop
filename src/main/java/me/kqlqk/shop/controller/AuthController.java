package me.kqlqk.shop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.dto.LoginDTO;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final UserService userService;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(UserService userService, AuthenticationManager authManager, JwtUtil jwtUtil) {
        this.userService = userService;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("loginDTO", new LoginDTO());

        return "auth/LoginPage";
    }

    @PostMapping("/login")
    public String logIn(@ModelAttribute(name = "loginDTO") LoginDTO loginDTO, HttpServletResponse response) {
        // TODO: 24/06/2023 Handle bad credentials
        User user = userService.getByEmail(loginDTO.getEmail());
        authManager.authenticate(new UsernamePasswordAuthenticationToken(user, loginDTO.getPassword()));

        Cookie cookie = new Cookie("accessToken", jwtUtil.generateAccessToken(loginDTO.getEmail()));
        cookie.setPath("/");
        cookie.setMaxAge(36000);
        response.addCookie(cookie);

        Cookie cookie2 = new Cookie("userId", String.valueOf(user.getId()));
        cookie2.setPath("/");
        cookie2.setMaxAge(36000);
        response.addCookie(cookie2);

        jwtUtil.generateAndSaveOrUpdateRefreshToken(loginDTO.getEmail());

        return "redirect:/user/" + user.getId();
    }
}

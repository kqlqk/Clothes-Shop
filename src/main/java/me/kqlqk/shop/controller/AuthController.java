package me.kqlqk.shop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.kqlqk.shop.dto.LoginDTO;
import me.kqlqk.shop.dto.RegistrationDTO;
import me.kqlqk.shop.exception.BadCredentialsException;
import me.kqlqk.shop.exception.UserExistsException;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.JwtUtil;
import me.kqlqk.shop.util.LoginErrorParam;
import me.kqlqk.shop.util.RegistrationErrorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
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
    public String getLoginPage(Model model, @RequestParam(value = "error", required = false) String errorParam) {
        model.addAttribute("loginDTO", new LoginDTO());
        model.addAttribute("errorParam", errorParam);

        return "auth/LoginPage";
    }

    @PostMapping("/login")
    public String logIn(@Valid @ModelAttribute(name = "loginDTO") LoginDTO loginDTO,
                        BindingResult bindingResult,
                        HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "auth/LoginPage";
        }

        User user;
        try {
            user = userService.getByEmail(loginDTO.getEmail());
            authManager.authenticate(new UsernamePasswordAuthenticationToken(user, loginDTO.getPassword()));
        }
        catch (RuntimeException e) {
            if (e instanceof BadCredentialsException || e instanceof UserNotFoundException) {
                log.info("Bad credentials for user ", e);
                return "redirect:/login?error=" + LoginErrorParam.BAD_CREDENTIALS;
            }
            else {
                log.warn("Something went wrong while login ", e);
                return "redirect:/login?error=" + LoginErrorParam.UNKNOWN;
            }
        }

        Cookie cookie = new Cookie("accessToken", jwtUtil.generateAccessToken(loginDTO.getEmail()));
        cookie.setPath("/");
        cookie.setMaxAge(36000);
        response.addCookie(cookie);

        jwtUtil.generateAndSaveOrUpdateRefreshToken(loginDTO.getEmail());

        return "redirect:/user/" + user.getId();
    }

    @GetMapping("/registration")
    public String getRegistrationPage(Model model, @RequestParam(value = "error", required = false) String errorParam) {
        model.addAttribute("registrationDTO", new RegistrationDTO());
        model.addAttribute("errorParam", errorParam);

        return "auth/RegistrationPage";
    }

    @PostMapping("/registration")
    public String signUp(@Valid @ModelAttribute(name = "registrationDTO") RegistrationDTO registrationDTO,
                         BindingResult bindingResult,
                         HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "auth/RegistrationPage";
        }

        User user = new User();
        user.setEmail(registrationDTO.getEmail());
        user.setName(registrationDTO.getName());
        user.setPassword(registrationDTO.getPassword());

        try {
            userService.add(user);
        }
        catch (RuntimeException e) {
            if (e instanceof UserExistsException) {
                log.info("User already exists", e);
                return "redirect:/registration?error=" + RegistrationErrorParam.USER_EXISTS;
            }
            else {
                log.warn("Something went wrong while registration ", e);
                return "redirect:/registration?error=" + RegistrationErrorParam.UNKNOWN;
            }
        }

        Cookie cookie = new Cookie("accessToken", jwtUtil.generateAccessToken(user.getEmail()));
        cookie.setPath("/");
        cookie.setMaxAge(36000);
        response.addCookie(cookie);

        jwtUtil.generateAndSaveOrUpdateRefreshToken(registrationDTO.getEmail());

        return "redirect:/user/" + userService.getByEmail(registrationDTO.getEmail()).getId();
    }
}

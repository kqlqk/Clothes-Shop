package me.kqlqk.shop.controller;

import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.dto.LoginDTO;
import me.kqlqk.shop.dto.RegistrationDTO;
import me.kqlqk.shop.exception.BadCredentialsException;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.service.RefreshTokenService;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.LoginErrorParam;
import me.kqlqk.shop.util.RegistrationErrorParam;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ModelMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @MockBean
    private AuthenticationManager authManager;

    @SpyBean
    private UserService userService;

    @Test
    public void getLoginPage_shouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("auth/LoginPage"))
                .andExpect(model().attributeExists("loginDTO"));

        mockMvc.perform(get("/login?error=UNKNOWN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("auth/LoginPage"))
                .andExpect(model().attributeExists("loginDTO"));
    }

    @Test
    public void logIn_shouldLogInUser() throws Exception {
        when(authManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("email@email.com", "password1"));

        ModelMap model = new ModelMap();
        LoginDTO loginDTO = new LoginDTO("email@email.com", "Password1");
        model.addAttribute("loginDTO", loginDTO);

        mockMvc.perform(post("/login")
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(redirectedUrl("/user/1"));

        assertThat(refreshTokenService.getByUserEmail("email@email.com").getToken()).isNotEqualTo("token1");
    }

    @Test
    public void logIn_shouldThrowException() throws Exception {
        when(authManager.authenticate(any())).thenThrow(new BadCredentialsException("Bad credentials"),
                new UserNotFoundException("User not found"),
                new RuntimeException("Random exception"));

        ModelMap model = new ModelMap();
        LoginDTO loginDTO = new LoginDTO("email@email.com", "Password1");
        model.addAttribute("loginDTO", loginDTO);

        mockMvc.perform(post("/login")
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().doesNotExist("accessToken"))
                .andExpect(redirectedUrl("/login?error=" + LoginErrorParam.BAD_CREDENTIALS));

        mockMvc.perform(post("/login")
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().doesNotExist("accessToken"))
                .andExpect(redirectedUrl("/login?error=" + LoginErrorParam.BAD_CREDENTIALS));

        mockMvc.perform(post("/login")
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().doesNotExist("accessToken"))
                .andExpect(redirectedUrl("/login?error=" + LoginErrorParam.UNKNOWN));
    }

    @Test
    public void getRegistrationPage_shouldReturnRegistrationPage() throws Exception {
        mockMvc.perform(get("/registration"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("auth/RegistrationPage"))
                .andExpect(model().attributeExists("registrationDTO"));


        mockMvc.perform(get("/registration?error=UNKNOWN"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("auth/RegistrationPage"))
                .andExpect(model().attributeExists("registrationDTO"))
                .andExpect(model().attributeExists("errorParam"));
    }

    @Test
    public void signUp_shouldSignUpUser() throws Exception {
        ModelMap model = new ModelMap();
        RegistrationDTO registrationDTO = new RegistrationDTO("email3@email.com", "name", "Password1");
        model.addAttribute("registrationDTO", registrationDTO);

        mockMvc.perform(post("/registration")
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(redirectedUrl("/user/3"));

        assertThat(refreshTokenService.getByUserEmail("email3@email.com")).isNotNull();
    }

    @Test
    public void signUp_shouldThrowException() throws Exception {
        ModelMap model = new ModelMap();
        RegistrationDTO registrationDTO = new RegistrationDTO("email@email.com", "name", "Password1");
        model.addAttribute("registrationDTO", registrationDTO);

        mockMvc.perform(post("/registration")
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().doesNotExist("accessToken"))
                .andExpect(redirectedUrl("/registration?error=" + RegistrationErrorParam.USER_EXISTS));

        doThrow(new RuntimeException("Random exception"))
                .when(userService)
                .add(any());

        registrationDTO = new RegistrationDTO("email3@email.com", "name", "Password1");
        model.addAttribute("registrationDTO", registrationDTO);

        mockMvc.perform(post("/registration")
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().doesNotExist("accessToken"))
                .andExpect(redirectedUrl("/registration?error=" + RegistrationErrorParam.UNKNOWN));
    }
}

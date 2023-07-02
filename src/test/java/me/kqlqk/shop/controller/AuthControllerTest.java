package me.kqlqk.shop.controller;

import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.dto.LoginDTO;
import me.kqlqk.shop.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ModelMap;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    public void getLoginPage_shouldReturnLoginPage() throws Exception {
        mockMvc.perform(get("/login"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("auth/LoginPage"))
                .andExpect(model().attributeExists("loginDTO"));
    }

    @Test
    public void logIn_shouldLogInUser() throws Exception {
        when(authManager.authenticate(any())).thenReturn(new UsernamePasswordAuthenticationToken("email@email.com", "password1"));

        ModelMap model = new ModelMap();
        LoginDTO loginDTO = new LoginDTO("email@email.com", "password1");
        model.addAttribute("loginDTO", loginDTO);

        mockMvc.perform(post("/login")
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(cookie().exists("accessToken"))
                .andExpect(redirectedUrl("/user/1"));

        assertThat(refreshTokenService.getByUserEmail("email@email.com").getToken()).isNotEqualTo("token1");
    }
}

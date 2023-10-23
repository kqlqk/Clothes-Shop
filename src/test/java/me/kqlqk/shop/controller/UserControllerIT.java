package me.kqlqk.shop.controller;

import jakarta.servlet.http.Cookie;
import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.dto.AddressDTO;
import me.kqlqk.shop.dto.CombinedDTO;
import me.kqlqk.shop.dto.UserDTO;
import me.kqlqk.shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ModelMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
@ExtendWith(MockitoExtension.class)
public class UserControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private Cookie accessToken;

    @Value("${admin.emails}")
    private String[] emails;

    @BeforeEach
    public void beforeEach() {
        accessToken = new Cookie("accessToken", jwtUtil.generateAccessToken(emails[0]));
    }

    @Test
    public void getUserPage_shouldReturnUserPage() throws Exception {
        mockMvc.perform(get("/user/1")
                        .cookie(accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/UserPage"))
                .andExpect(model().attributeExists("currentOrders"))
                .andExpect(model().attributeExists("realisedOrders"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("combinedDTO"));
    }

    @Test
    public void editUserPage_shouldEditUserPage() throws Exception {
        CombinedDTO combinedDTO = new CombinedDTO();
        combinedDTO.setAddressDTO(new AddressDTO("contry", "city", "street", "house", "32100"));
        combinedDTO.setUserDTO(new UserDTO("newEmail@mail..com", ""));

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute(combinedDTO);

        mockMvc.perform(put("/user/1")
                        .cookie(accessToken)
                        .flashAttrs(modelMap))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/1"));
    }

}

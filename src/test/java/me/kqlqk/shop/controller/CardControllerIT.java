package me.kqlqk.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;
import me.kqlqk.shop.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.ModelMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
public class CardControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Test
    public void getCardPage_shouldReturnCardPage() throws Exception {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L");

        mockMvc.perform(get("/card")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/CardPage"))
                .andExpect(model().attributeExists("totalPrice"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("newOrderDTO"))
                .andExpect(model().attributeExists("ordersJson"))
                .andExpect(model().attributeExists("newOrderJsonDTO"));

        mockMvc.perform(get("/card?error=UNKNOWN")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/CardPage"))
                .andExpect(model().attributeExists("totalPrice"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("newOrderDTO"))
                .andExpect(model().attributeExists("ordersJson"))
                .andExpect(model().attributeExists("newOrderJsonDTO"))
                .andExpect(model().attributeExists("error"));
    }

    @Test
    public void addProductToCard_shouldAddProductToCard() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, 1, Colors.WHITE, Sizes.XL, false);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(orderDTO);

        mockMvc.perform(post("/card")
                        .content(json))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/card"));
    }

    @Test
    public void deleteProductFromCard_shouldDeleteProductFromCard() throws Exception {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L");

        mockMvc.perform(delete("/card")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/card"));


        Cookie accessToken = new Cookie("accessToken", jwtUtil.generateAccessToken("email@email.com"));
        OrderDTO orderDTO = new OrderDTO(1, 1, Colors.BLACK, Sizes.M, true);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("newOrderDTO", orderDTO);

        mockMvc.perform(delete("/card")
                        .cookie(accessToken)
                        .flashAttrs(modelMap))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/card"));
    }
}

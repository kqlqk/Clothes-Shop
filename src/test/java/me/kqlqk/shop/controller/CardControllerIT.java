package me.kqlqk.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.dto.ProductBuyingDTO;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
public class CardControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getCardPage_shouldReturnCardPage() throws Exception {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L");

        mockMvc.perform(get("/card")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("CardPage"))
                .andExpect(model().attributeExists("totalPrice"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("productBuyingDTO"));
    }

    @Test
    public void addProductToCard_shouldAddProductToCard() throws Exception {
        ProductBuyingDTO productBuyingDTO = new ProductBuyingDTO(1, 1, Colors.WHITE, Sizes.XL);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(productBuyingDTO);

        mockMvc.perform(post("/card")
                        .content(json))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/card"));
    }

    @Test
    public void deleteProductFromCard_shouldDeleteProductFromCard() throws Exception {
        Cookie cookie = new Cookie("product", "1/1/BLACK/L.2/2/GRAY/L");
        ProductBuyingDTO productBuyingDTO = new ProductBuyingDTO(1, 1, Colors.BLACK, Sizes.L);
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(productBuyingDTO);

        mockMvc.perform(delete("/card")
                        .cookie(cookie)
                        .content(json))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/card"));
    }
}

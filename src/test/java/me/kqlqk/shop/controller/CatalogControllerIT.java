package me.kqlqk.shop.controller;

import me.kqlqk.shop.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
public class CatalogControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getPageWithAllProducts_shouldReturnPageWithAllProducts() throws Exception {
        mockMvc.perform(get("/catalog"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/CatalogPage"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("productsDiscounts"));
    }
}

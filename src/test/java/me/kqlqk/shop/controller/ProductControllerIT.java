package me.kqlqk.shop.controller;

import me.kqlqk.shop.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
public class ProductControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getProductPage_shouldReturnProductPage() throws Exception {
        mockMvc.perform(get("/catalog/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/ProductPage"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attributeExists("files"));
    }
}

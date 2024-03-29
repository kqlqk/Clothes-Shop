package me.kqlqk.shop.controller;

import me.kqlqk.shop.ControllerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
public class MainControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void getMainPage_shouldReturnMainPage() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("MainPage"))
                .andExpect(model().attributeExists("saleProducts"))
                .andExpect(model().attributeExists("newProducts"))
                .andExpect(model().attributeExists("saleProductsDiscounts"))
                .andExpect(model().attributeExists("newProductsDiscounts"));
    }

    @Test
    public void search_shouldSearchProducts() throws Exception {
        mockMvc.perform(get("/search")
                        .param("search", "white"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/SearchPage"));

        mockMvc.perform(get("/search"))
                .andDo(print())
                .andExpect(status().is4xxClientError());
    }

}

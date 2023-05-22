package me.kqlqk.shop.controller;

import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.model.Product;
import me.kqlqk.shop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
public class CatalogControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void getPageWithAllProducts_shouldReturnPageWithAllProducts() throws Exception {
        List<Product> mockProducts = new ArrayList<>();
        Mockito.when(productService.getAll()).thenReturn(mockProducts);

        mockMvc.perform(get("/catalog"))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/CatalogPage"))
                .andExpect(model().attribute("products", mockProducts));
    }
}

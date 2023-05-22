package me.kqlqk.shop.controller;

import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.model.Product;
import me.kqlqk.shop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
public class ProductControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    public void getProductPage_shouldReturnProductPage() throws Exception {
        long productId = 1;
        Product mockProduct = new Product();
        mockProduct.setPath("test");
        when(productService.getById(productId)).thenReturn(mockProduct);

        mockMvc.perform(get("/catalog/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(view().name("catalog/ProductPage"))
                .andExpect(model().attribute("product", mockProduct))
                .andExpect(model().attributeExists("files"));
    }
}

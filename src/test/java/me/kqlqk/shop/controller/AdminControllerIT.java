package me.kqlqk.shop.controller;

import jakarta.servlet.http.Cookie;
import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.dto.ProductDTO;
import me.kqlqk.shop.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
@ExtendWith(MockitoExtension.class)
public class AdminControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private Cookie accessToken;

    @Value("${admin.email}")
    private String email;

    @BeforeEach
    public void beforeEach() {
        accessToken = new Cookie("accessToken", jwtUtil.generateAccessToken(email));
    }

    @Test
    public void getAdminPage_shouldReturnAdminPage() throws Exception {
        mockMvc.perform(get("/admin")
                        .cookie(accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/AdminPage"));
    }

    @Test
    public void getAdminOrdersPage_shouldReturnAdminOrdersPage() throws Exception {
        mockMvc.perform(get("/admin/orders")
                        .cookie(accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/AdminOrderPage"))
                .andExpect(model().attributeExists("unrealisedOrders"))
                .andExpect(model().attributeExists("realisedOrders"));
    }

    @Test
    public void getAdminProductPage_shouldReturnAdminProductPage() throws Exception {
        mockMvc.perform(get("/admin/product")
                        .cookie(accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/AdminProductPage"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @Transactional
    public void getAdminEditProductPage_shouldReturnAdminEditProductPage() throws Exception {
        mockMvc.perform(get("/admin/product/edit/1")
                        .cookie(accessToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("admin/AdminEditProductPage"))
                .andExpect(model().attributeExists("id"))
                .andExpect(model().attributeExists("productDTO"))
                .andExpect(model().attributeExists("sizes"))
                .andExpect(model().attributeExists("colors"));
    }

    @Test
    public void editProduct_shouldEditProduct() throws Exception {
        ModelMap model = new ModelMap();
        ProductDTO productDTO = new ProductDTO();
        productDTO.setName("");
        productDTO.setDiscount(1);
        productDTO.setPrice(222);
        productDTO.setDescription("");
        productDTO.setPath("");

        model.addAttribute("productDTO", productDTO);

        mockMvc.perform(put("/admin/product/edit/1")
                        .cookie(accessToken)
                        .flashAttrs(model))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/product"));
    }


}

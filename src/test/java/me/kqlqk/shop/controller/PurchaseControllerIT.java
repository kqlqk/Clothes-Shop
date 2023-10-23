package me.kqlqk.shop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import me.kqlqk.shop.ControllerTest;
import me.kqlqk.shop.dto.AddressDTO;
import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.dto.OrderJsonDTO;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.service.ProductService;
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

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ControllerTest
@ExtendWith(MockitoExtension.class)
public class PurchaseControllerIT {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    private Cookie accessToken;

    @Value("${admin.emails}")
    private String[] emails;

    @Autowired
    private PurchaseController purchaseController;

    @Autowired
    private ProductService productService;

    @BeforeEach
    public void beforeEach() {
        accessToken = new Cookie("accessToken", jwtUtil.generateAccessToken(emails[0]));
    }

    @Test
    public void redirectFromBuyNowButton_shouldRedirectFromBuyNowButton() throws Exception {
        OrderDTO orderDTO = new OrderDTO(1, 1, Colors.BLACK, Sizes.M, false);
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute(orderDTO);


        mockMvc.perform(post("/purchase/temp")
                        .cookie(accessToken)
                        .flashAttrs(modelMap))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/TempPage"))
                .andExpect(model().attributeExists("ordersJson"))
                .andExpect(model().attributeExists("newOrderJsonDTO"));
    }

    @Test
    public void chooseAddress_shouldChooseAddress() throws Exception {
        OrderJsonDTO orderJsonDTO = new OrderJsonDTO("json");
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("newOrderJsonDTO", orderJsonDTO);


        mockMvc.perform(post("/purchase/address")
                        .cookie(accessToken)
                        .flashAttrs(modelMap))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/AddressPage"))
                .andExpect(model().attributeExists("ordersJson"))
                .andExpect(model().attributeExists("newOrderJsonDTO"))
                .andExpect(model().attributeExists("addressOption"));
    }

    @Test
    @Transactional
    public void choosePayment_shouldChoosePayment() throws Exception {
        PurchaseController.AddressOption addressOption = purchaseController.new AddressOption();
        addressOption.setOption("new");
        addressOption.setCountry("country");
        addressOption.setCity("city");
        addressOption.setStreet("street");
        addressOption.setHouse("house");
        addressOption.setPostalCode("53454");

        List<OrderDTO> orders = new ArrayList<>();
        OrderDTO orderDTO = new OrderDTO(1, 1, Colors.BLACK, Sizes.M, false);
        Product product = productService.getById(orderDTO.getProductId());
        orderDTO.setProduct(product);
        orderDTO.setColor(product.getColors().stream()
                .filter(e -> e.getName().equals(orderDTO.getColorName()))
                .findFirst()
                .get());
        orderDTO.setSize(product.getSizes().stream()
                .filter(e -> e.getName().equals(orderDTO.getSizeName()))
                .findFirst()
                .get());
        orders.add(orderDTO);
        OrderJsonDTO orderJsonDTO = new OrderJsonDTO(new ObjectMapper().writeValueAsString(orders));

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute(addressOption);
        modelMap.addAttribute("newOrderJsonDTO", orderJsonDTO);


        mockMvc.perform(post("/purchase/payment")
                        .cookie(accessToken)
                        .flashAttrs(modelMap))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/PaymentPage"))
                .andExpect(model().attributeExists("ordersJson"))
                .andExpect(model().attributeExists("newOrderJsonDTO"))
                .andExpect(model().attributeExists("paymentOption"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    @Test
    @Transactional
    public void confirmOrder_shouldConfirmOrder() throws Exception {
        PurchaseController.PaymentOption paymentOption = purchaseController.new PaymentOption();
        paymentOption.setOption("crypto");

        List<OrderDTO> orders = new ArrayList<>();
        OrderDTO orderDTO = new OrderDTO(1, 1, Colors.BLACK, Sizes.M, false);
        Product product = productService.getById(orderDTO.getProductId());
        orderDTO.setProduct(product);
        orderDTO.setColor(product.getColors().stream()
                .filter(e -> e.getName().equals(orderDTO.getColorName()))
                .findFirst()
                .get());
        orderDTO.setSize(product.getSizes().stream()
                .filter(e -> e.getName().equals(orderDTO.getSizeName()))
                .findFirst()
                .get());
        orderDTO.setAddressDTO(new AddressDTO("country", "city", "street", "house", "0000"));
        orders.add(orderDTO);
        OrderJsonDTO orderJsonDTO = new OrderJsonDTO(new ObjectMapper().writeValueAsString(orders));

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute(paymentOption);
        modelMap.addAttribute("newOrderJsonDTO", orderJsonDTO);
        modelMap.addAttribute("totalPrice", 10);


        mockMvc.perform(post("/purchase/confirm")
                        .cookie(accessToken)
                        .flashAttrs(modelMap))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("purchase/ConfirmPage"))
                .andExpect(model().attributeExists("ordersJson"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(model().attributeExists("newOrderJsonDTO"))
                .andExpect(model().attributeExists("paymentMethod"))
                .andExpect(model().attributeExists("totalPrice"));
    }

    @Test
    @Transactional
    public void redirectToPay_shouldRedirectToPay() throws Exception {
        List<OrderDTO> orders = new ArrayList<>();
        OrderDTO orderDTO = new OrderDTO(1, 1, Colors.BLACK, Sizes.M, false);
        Product product = productService.getById(orderDTO.getProductId());
        orderDTO.setProduct(product);
        orderDTO.setColor(product.getColors().stream()
                .filter(e -> e.getName().equals(orderDTO.getColorName()))
                .findFirst()
                .get());
        orderDTO.setSize(product.getSizes().stream()
                .filter(e -> e.getName().equals(orderDTO.getSizeName()))
                .findFirst()
                .get());
        orderDTO.setAddressDTO(new AddressDTO("country", "city", "street", "house", "0000"));
        orders.add(orderDTO);
        OrderJsonDTO orderJsonDTO = new OrderJsonDTO(new ObjectMapper().writeValueAsString(orders));

        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("newOrderJsonDTO", orderJsonDTO);

        mockMvc.perform(post("/purchase/redirect?paymentOption=crypto")
                        .cookie(accessToken)
                        .flashAttrs(modelMap))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/purchase/done"));
    }

}

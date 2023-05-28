package me.kqlqk.shop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.dto.ProductBuyingDTO;
import me.kqlqk.shop.service.ProductService;
import me.kqlqk.shop.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/card")
public class CardController {
    private final ProductService productService;

    @Autowired
    public CardController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping
    public String getCardPage(HttpServletRequest request, Model model) {
        Cookie cookie = null;

        if (request.getCookies() != null) {
            cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().equals("product"))
                    .findFirst()
                    .orElse(null);
        }

        List<OrderDTO> orders = new ArrayList<>();

        if (CookieUtil.containsProductBuyingDTO(cookie)) {
            List<ProductBuyingDTO> productBuyingDTOs = CookieUtil.getProductBuyingDTOs(cookie);

            for (ProductBuyingDTO p : productBuyingDTOs) {
                orders.add(new OrderDTO(productService.getById(p.getProductId()), p));
            }
        }

        AtomicInteger totalPrice = new AtomicInteger();
        orders.forEach(o -> totalPrice.addAndGet(o.getProduct().getPrice()));

        model.addAttribute("totalPrice", totalPrice.intValue());
        model.addAttribute("orders", orders);
        model.addAttribute("productBuyingDTO", new ProductBuyingDTO());

        return "CardPage";
    }

    @PostMapping
    public String addProductToCard(@ModelAttribute("productDTO") ProductBuyingDTO productBuyingDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        boolean cookieExits = false;

        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals("product")) {
                    int id = CookieUtil.getLastIdFromProductBuyingDTOs(c) + 1;
                    cookieExits = true;
                    c.setPath("/");
                    c.setValue(c.getValue() + id + "/" + productBuyingDTO.getProductId() + "/" + productBuyingDTO.getColor() + "/" + productBuyingDTO.getSize() + ".");
                    c.setMaxAge(36000); //10h
                    response.addCookie(c);
                }
            }
        }

        if (!cookieExits) {
            Cookie cookie = new Cookie("product", 1 + "/" +
                    productBuyingDTO.getProductId() + "/" + productBuyingDTO.getColor() + "/" + productBuyingDTO.getSize() + ".");
            cookie.setPath("/");
            cookie.setMaxAge(36000);
            response.addCookie(cookie);
        }

        return "redirect:/card";
    }

    @DeleteMapping
    public String deleteProductFromCard(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @ModelAttribute("productBuyingDTO") ProductBuyingDTO productBuyingDTO) {
        Cookie cookie = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("product"))
                .findFirst()
                .orElse(null);

        CookieUtil.deleteProductBuyingDTO(cookie, productBuyingDTO);

        response.addCookie(cookie);
        return "redirect:/card";
    }

}

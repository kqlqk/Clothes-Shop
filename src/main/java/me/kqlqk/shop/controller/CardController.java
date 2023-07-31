package me.kqlqk.shop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.dto.ProductBuyingDTO;
import me.kqlqk.shop.model.Card;
import me.kqlqk.shop.model.product.Color;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.model.product.Size;
import me.kqlqk.shop.model.user.User;
import me.kqlqk.shop.service.CardService;
import me.kqlqk.shop.service.ProductService;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.CookieUtil;
import me.kqlqk.shop.util.JwtUtil;
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
    private final CardService cardService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public CardController(ProductService productService, CardService cardService, JwtUtil jwtUtil, UserService userService) {
        this.productService = productService;
        this.cardService = cardService;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
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
        Cookie cookie = CookieUtil.getCookieByName("accessToken", request);

        if (cookie != null && jwtUtil.validateAccessToken(cookie.getValue())) {
            String email = jwtUtil.getEmailFromAccessToken(cookie.getValue());
            User user = userService.getByEmail(email);  // TODO: 31/07/2023 add checking

            Product product = productService.getById(productBuyingDTO.getProductId());
            Color color = product.getColors().stream()
                    .filter(e -> e.getName().equals(productBuyingDTO.getColor()))
                    .findFirst()
                    .get();
            Size size = product.getSizes().stream()
                    .filter(e -> e.getName().equals(productBuyingDTO.getSize()))
                    .findFirst()
                    .get();

            Card card = new Card(color, size, product, user);
            cardService.add(card);
        }
        else {
            boolean cookieExits = false;

            if (request.getCookies() != null) {
                for (Cookie c : request.getCookies()) {
                    if (c.getName().equals("product")) {
                        int id = CookieUtil.getLastIdFromProductBuyingDTOs(c) + 1;
                        cookieExits = true;
                        c.setPath("/");
                        c.setValue(c.getValue() + id + "/" +
                                productBuyingDTO.getProductId() + "/" +
                                productBuyingDTO.getColor() + "/" +
                                productBuyingDTO.getSize() + ".");
                        c.setMaxAge(36000); //10h
                        response.addCookie(c);
                    }
                }
            }

            if (!cookieExits) {
                Cookie newCookie = new Cookie("product", 1 + "/" +
                        productBuyingDTO.getProductId() + "/" +
                        productBuyingDTO.getColor() + "/" +
                        productBuyingDTO.getSize() + ".");
                newCookie.setPath("/");
                newCookie.setMaxAge(36000);
                response.addCookie(newCookie);
            }

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

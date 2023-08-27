package me.kqlqk.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.dto.OrderJsonDTO;
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
    public String getCardPage(HttpServletRequest request, Model model, HttpServletResponse response) throws JsonProcessingException {
        List<OrderDTO> orders = new ArrayList<>();

        try {
            String email = getEmailAndUpdateTokenIfRequired(request, response);
            User user = userService.getByEmail(email);

            if (cardService.existsByUser(user)) {
                cardService.getByUser(user).forEach(e -> orders.add(OrderDTO.convertToOrderDTO(e)));
            }
        }
        catch (RuntimeException ex) {
            Cookie productCookie = CookieUtil.getCookieByName("product", request);

            if (CookieUtil.containsOrderDTO(productCookie)) {
                CookieUtil.getOrderDTOs(productCookie).forEach(e -> {
                    Product p = productService.getById(e.getProductId());
                    e.setProduct(p);
                    e.setColor(p.getColors()
                            .stream()
                            .filter(c -> c.getName().equals(e.getColorName()))
                            .findFirst()
                            .get());
                    e.setSize(p.getSizes()
                            .stream()
                            .filter(c -> c.getName().equals(e.getSizeName()))
                            .findFirst()
                            .get());
                    orders.add(e);
                });
            }
        }

        AtomicInteger totalPrice = new AtomicInteger();
        orders.forEach(e -> totalPrice.addAndGet(e.getProduct().getPrice()));

        model.addAttribute("totalPrice", totalPrice.intValue());
        model.addAttribute("orders", orders);
        model.addAttribute("newOrderDTO", new OrderDTO());
        model.addAttribute("ordersJson", new ObjectMapper().writeValueAsString(orders));
        model.addAttribute("newOrderJsonDTO", new OrderJsonDTO());

        return "user/CardPage";
    }

    @PostMapping
    public String addProductToCard(@ModelAttribute("orderDTO") OrderDTO orderDTO,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {
        try {
            String email = getEmailAndUpdateTokenIfRequired(request, response);
            User user = userService.getByEmail(email);

            Product product = productService.getById(orderDTO.getProductId());
            Color color = product.getColors().stream()
                    .filter(e -> e.getName().equals(orderDTO.getColorName()))
                    .findFirst()
                    .get();
            Size size = product.getSizes().stream()
                    .filter(e -> e.getName().equals(orderDTO.getSizeName()))
                    .findFirst()
                    .get();

            Card card = new Card(color, size, product, user);
            cardService.add(card);
        }
        catch (RuntimeException ex) {
            boolean cookieExits = false;

            Cookie c = CookieUtil.getCookieByName("product", request);
            if (request.getCookies() != null && c != null) {
                int id = CookieUtil.getLastIdFromOrderDTOs(c) + 1;
                cookieExits = true;
                c.setPath("/");
                c.setValue(c.getValue() + id + "/" +
                        orderDTO.getProductId() + "/" +
                        orderDTO.getColorName() + "/" +
                        orderDTO.getSizeName() + ".");
                c.setMaxAge(10 * 365 * 24 * 60 * 60); //10years

                response.addCookie(c);
            }

            if (!cookieExits) {
                Cookie newCookie = new Cookie("product", 1 + "/" +
                        orderDTO.getProductId() + "/" +
                        orderDTO.getColorName() + "/" +
                        orderDTO.getSizeName() + ".");
                newCookie.setPath("/");
                newCookie.setMaxAge(10 * 365 * 24 * 60 * 60);

                response.addCookie(newCookie);
            }
        }

        return "redirect:/card";
    }

    @DeleteMapping
    public String deleteProductFromCard(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @ModelAttribute("newOrderDTO") OrderDTO orderDTO) {
        try {
            getEmailAndUpdateTokenIfRequired(request, response);

            if (orderDTO.isAuthorized()) {
                cardService.remove(cardService.getById(orderDTO.getId()));
            }
            else {
                throw new RuntimeException("Unauthorized");
            }
        }
        catch (RuntimeException ex) {
            Cookie cookie = CookieUtil.getCookieByName("product", request);

            CookieUtil.deleteOrderDTO(cookie, orderDTO);

            response.addCookie(cookie);
        }

        return "redirect:/card";
    }

    private String getEmailAndUpdateTokenIfRequired(HttpServletRequest request, HttpServletResponse response) {
        Cookie accessTokenCookie = CookieUtil.getCookieByName("accessToken", request);
        String accessToken = null;
        boolean accessTokenValid = false;

        if (accessTokenCookie != null) {
            accessToken = accessTokenCookie.getValue();
            accessTokenValid = jwtUtil.validateAccessToken(accessToken);
        }

        if (!accessTokenValid) {
            accessToken = jwtUtil.updateAccessTokenIfExpired(accessToken);

            Cookie cookie = new Cookie("accessToken", accessToken);
            cookie.setPath("/");
            cookie.setMaxAge(10 * 365 * 24 * 60 * 60);
            response.addCookie(cookie);
        }

        return jwtUtil.getEmailFromAccessToken(accessToken);
    }

}

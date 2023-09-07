package me.kqlqk.shop.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import me.kqlqk.shop.dto.AddressDTO;
import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.dto.OrderJsonDTO;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.model.user.Address;
import me.kqlqk.shop.model.user.User;
import me.kqlqk.shop.service.OrderService;
import me.kqlqk.shop.service.ProductService;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.CookieUtil;
import me.kqlqk.shop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public PurchaseController(JwtUtil jwtUtil, UserService userService, OrderService orderService, ProductService productService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.orderService = orderService;
        this.productService = productService;
    }

    @PostMapping("/temp")
    public String redirect(Model model, @ModelAttribute("orderDTO") OrderDTO orderDTO) throws JsonProcessingException {
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

        List<OrderDTO> orders = new ArrayList<>();
        orders.add(orderDTO);

        model.addAttribute("ordersJson", new ObjectMapper().writeValueAsString(orders));
        model.addAttribute("newOrderJsonDTO", new OrderJsonDTO());

        return "purchase/TempPage";
    }

    @PostMapping("/address")
    public String chooseAddress(HttpServletRequest request, HttpServletResponse response,
                                Model model, @ModelAttribute("newOrderJsonDTO") OrderJsonDTO orderJsonDTO) {
        String email;
        try {
            email = getEmailAndUpdateTokenIfRequired(request, response);
            User user = userService.getByEmail(email);

            model.addAttribute("user", user);
        }
        catch (RuntimeException e) {
            if (!(e instanceof UserNotFoundException || e instanceof NullPointerException)) {
                return "redirect:/card?error=STH_WRONG";
            }
        }

        model.addAttribute("ordersJson", orderJsonDTO.getOrderJson());
        model.addAttribute("newOrderJsonDTO", new OrderJsonDTO());
        model.addAttribute("addressOption", new AddressOption());

        return "purchase/AddressPage";
    }

    @PostMapping("/payment")
    public String choosePayment(HttpServletRequest request, HttpServletResponse response,
                                Model model, @ModelAttribute("addressOption") AddressOption addressOption,
                                @ModelAttribute("newOrderJsonDTO") OrderJsonDTO orderJsonDTO) throws JsonProcessingException {
        try {
            getEmailAndUpdateTokenIfRequired(request, response);
        }
        catch (RuntimeException e) {
            if (!(e instanceof UserNotFoundException || e instanceof NullPointerException)) {
                return "redirect:/card?error=STH_WRONG";
            }
        }

        List<OrderDTO> orderDTOs = new ObjectMapper().readValue(orderJsonDTO.getOrderJson(), new TypeReference<List<OrderDTO>>() {
        });
        AtomicInteger totalPrice = new AtomicInteger();

        orderDTOs.forEach(e ->
        {
            e.setAddressDTO(addressOption.getOption().equalsIgnoreCase("new") ?
                    new AddressDTO(addressOption.getCountry(), addressOption.getCity(),
                            addressOption.getStreet(), addressOption.getHouse(), addressOption.getPostalCode()) :
                    new AddressDTO(e.getUser().getAddress().getCountry(), e.getUser().getAddress().getCity(),
                            e.getUser().getAddress().getStreet(), e.getUser().getAddress().getHouse(),
                            e.getUser().getAddress().getPostalCode()));

            totalPrice.addAndGet(e.getProduct().getPrice());
        });
        orderJsonDTO.setOrderJson(new ObjectMapper().writeValueAsString(orderDTOs));

        model.addAttribute("ordersJson", orderJsonDTO.getOrderJson());
        model.addAttribute("newOrderJsonDTO", new OrderJsonDTO());
        model.addAttribute("paymentOption", new PaymentOption());
        model.addAttribute("totalPrice", totalPrice.intValue());

        return "purchase/PaymentPage";
    }

    @PostMapping("/confirm")
    public String confirmOrder(HttpServletRequest request, HttpServletResponse response,
                               Model model, @ModelAttribute("paymentOption") PaymentOption paymentOption,
                               @ModelAttribute("newOrderJsonDTO") OrderJsonDTO orderJsonDTO,
                               @ModelAttribute("totalPrice") int totalPrice) throws JsonProcessingException {
        List<OrderDTO> orderDTOs = new ObjectMapper().readValue(orderJsonDTO.getOrderJson(), new TypeReference<List<OrderDTO>>() {
        });

        try {
            getEmailAndUpdateTokenIfRequired(request, response);
        }
        catch (RuntimeException e) {
            if (!(e instanceof UserNotFoundException || e instanceof NullPointerException)) {
                return "redirect:/card?error=STH_WRONG";
            }
        }

        model.addAttribute("ordersJson", orderJsonDTO.getOrderJson());
        model.addAttribute("newOrderJsonDTO", new OrderJsonDTO());
        model.addAttribute("orders", orderDTOs);
        model.addAttribute("totalPrice", totalPrice);

        String paymentMethod = switch (paymentOption.getOption()) {
            case "card" -> "Credit/Debit card";
            case "paypal" -> "PayPal";
            case "crypto" -> "Crypto";
            default -> null;
        };

        model.addAttribute("paymentMethod", paymentMethod);

        return "purchase/ConfirmPage";
    }

    @PostMapping("/redirect")
    public String redirectToPay(HttpServletRequest request, HttpServletResponse response,
                                @ModelAttribute("newOrderJsonDTO") OrderJsonDTO orderJsonDTO,
                                @RequestParam("paymentOption") String paymentOption) throws JsonProcessingException {
        List<OrderDTO> orderDTOs = new ObjectMapper().readValue(orderJsonDTO.getOrderJson(), new TypeReference<List<OrderDTO>>() {
        });
        List<Order> orders = new ArrayList<>();

        try {
            getEmailAndUpdateTokenIfRequired(request, response);
        }
        catch (RuntimeException e) {
            if (!(e instanceof UserNotFoundException || e instanceof NullPointerException)) {
                return "redirect:/card?error=STH_WRONG";
            }
        }

        try {
            //do payment
        }
        catch (RuntimeException ignored) {
            return "redirect:/purchase/error";
        }

        orderDTOs.forEach(e -> orders.add(
                new Order(
                        e.getColor(),
                        e.getSize(),
                        e.getProduct(),
                        e.getUser(),
                        LocalDateTime.now(),
                        null,
                        (int) e.getId(),
                        false,
                        new Address(e.getAddressDTO().getCountry(), e.getAddressDTO().getCity(),
                                e.getAddressDTO().getStreet(), e.getAddressDTO().getHouse(),
                                e.getAddressDTO().getPostalCode()))));

        orders.forEach(orderService::add);

        return "redirect:/purchase/done";
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

    @Data
    public class PaymentOption {
        private String option;
    }

    @Data
    public class AddressOption {
        private String option;
        private String country;
        private String city;
        private String street;
        private String house;
        private String postalCode;
    }
}

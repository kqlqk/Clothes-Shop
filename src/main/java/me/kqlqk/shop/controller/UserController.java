package me.kqlqk.shop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.dto.AddressDTO;
import me.kqlqk.shop.dto.CombinedDTO;
import me.kqlqk.shop.dto.UserDTO;
import me.kqlqk.shop.exception.AddressException;
import me.kqlqk.shop.exception.OrderNotFoundException;
import me.kqlqk.shop.exception.UserExistsException;
import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.user.User;
import me.kqlqk.shop.service.OrderService;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.Formatter;
import me.kqlqk.shop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/{id}")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final OrderService orderService;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil, OrderService orderService) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.orderService = orderService;
    }

    @GetMapping
    public String getUserPage(@PathVariable long id, Model model,
                              @RequestParam(value = "errors", required = false) String[] errors) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Order> currentOrders;
        List<Order> realisedOrders;
        try {
            currentOrders = orderService.getByUserAndRealised(user, false);
        }
        catch (OrderNotFoundException e) {
            currentOrders = Collections.emptyList();
        }

        try {
            realisedOrders = orderService.getByUserAndRealised(user, true);
        }
        catch (OrderNotFoundException e) {
            realisedOrders = Collections.emptyList();
        }

        Map<Integer, List<Order>> groupedCurrentOrders = currentOrders.stream()
                .collect(Collectors.groupingBy(Order::getUuid));
        Map<Integer, List<Order>> groupedRealisedOrders = realisedOrders.stream()
                .collect(Collectors.groupingBy(Order::getUuid));

        if (errors != null) {
            model.addAttribute("errors", true);
            for (String s : errors) {
                String[] fieldValue = s.split("_");
                fieldValue[0] = fieldValue[0].split("\\.")[1];

                if (fieldValue.length > 1) {
                    model.addAttribute(fieldValue[0], fieldValue[1]);
                }
            }
        }

        model.addAttribute("currentOrders", groupedCurrentOrders.values());
        model.addAttribute("realisedOrders", groupedRealisedOrders.values());
        model.addAttribute("user", user);
        model.addAttribute("combinedDTO", new CombinedDTO());

        return "user/UserPage";
    }

    @PutMapping
    public String editUserPage(@PathVariable long id,
                               @ModelAttribute("combinedDTO") CombinedDTO combinedDTO,
                               HttpServletResponse response) {
        if (combinedDTO.allFieldsAreNullOrBlank()) {
            return "redirect:/user/" + id + "?errors=";
        }

        if (!combinedDTO.getUserDTO().getEmail().isBlank()) {
            combinedDTO.getUserDTO().setEmail(combinedDTO.getUserDTO().getEmail().toLowerCase());
        }

        String prefix = null;
        UserDTO userDTO = combinedDTO.getUserDTO();
        AddressDTO addressDTO = combinedDTO.getAddressDTO();

        User userDb = userService.getById(id);
        String oldEmail = userDb.getEmail();
        User user = new User();
        user.setId(id);
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());

        try {
            user.setAddress(Formatter.convertToSave(addressDTO, userDb.getAddress()));
            userService.update(user);

            if (!user.getEmail().isBlank() && !oldEmail.equalsIgnoreCase(user.getEmail())) {
                Cookie cookie = new Cookie("accessToken", jwtUtil.generateAccessToken(userDTO.getEmail()));
                cookie.setPath("/");
                cookie.setMaxAge(36000);
                response.addCookie(cookie);

                jwtUtil.updateRefreshTokenWithNewEmail(user.getEmail());
            }
        }
        catch (AddressException e) {
            prefix = "?errors=addressDTO.address_First time you should fill all address' form";
        }
        catch (UserExistsException e) {
            prefix = "?errors=addressDTO.address_Email already exists";
        }

        return "redirect:/user/" + id + (prefix == null ? "" : prefix);
    }
}

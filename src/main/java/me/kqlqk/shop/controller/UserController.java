package me.kqlqk.shop.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import me.kqlqk.shop.dto.UserDTO;
import me.kqlqk.shop.model.OrderHistory;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.Formatter;
import me.kqlqk.shop.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/{id}")
public class UserController {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public String getUserPage(@PathVariable long id, Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        model.addAttribute("currentOrder",
                user.getOrderHistory().stream().filter(e -> !e.isReleased()).collect(Collectors.toList()));

        List<OrderHistory> orderHistories = user.getOrderHistory().stream()
                .filter(OrderHistory::isReleased)
                .sorted((Comparator.comparing(OrderHistory::getDate)))
                .toList();

        Map<Long, List<OrderHistory>> groupedHistories = orderHistories.stream()
                .collect(Collectors.groupingBy(OrderHistory::getUuid));

        model.addAttribute("address", user.getAddress() == null || user.getAddress().isBlank() ? null : Formatter.formatAddressToShow(user.getAddress()));

        model.addAttribute("previousOrders", groupedHistories.values());
        model.addAttribute("user", user);
        model.addAttribute("userDTO", new UserDTO());

        return "UserPage";
    }

    @PutMapping
    public String editUserPage(@PathVariable long id, @ModelAttribute("userDTO") UserDTO userDTO,
                               HttpServletResponse response) {
        User userDb = userService.getById(id);
        String oldEmail = userDb.getEmail();
        User user = userDTO.convertToUser(userDb);
        user.setId(id);
        userService.update(user);

        if (!oldEmail.equalsIgnoreCase(user.getEmail())) {
            Cookie cookie = new Cookie("accessToken", jwtUtil.generateAccessToken(userDTO.getEmail()));
            cookie.setPath("/");
            cookie.setMaxAge(36000);
            response.addCookie(cookie);

            jwtUtil.updateRefreshTokenWithNewEmail(user.getEmail());
        }

        return "redirect:/user/" + id;
    }
}

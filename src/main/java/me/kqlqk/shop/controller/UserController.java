package me.kqlqk.shop.controller;

import me.kqlqk.shop.dto.UserDTO;
import me.kqlqk.shop.model.OrderHistory;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.service.UserService;
import me.kqlqk.shop.util.Formatter;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String getUserPage(@PathVariable long id, Model model) {
        User user = userService.getById(id);

        model.addAttribute("currentOrder",
                user.getOrderHistory().stream().filter(e -> !e.isReleased()).collect(Collectors.toList()));


        List<OrderHistory> orderHistories = user.getOrderHistory().stream()
                .filter(OrderHistory::isReleased)
                .sorted((Comparator.comparing(OrderHistory::getDate)))
                .toList();

        Map<Long, List<OrderHistory>> groupedHistories = orderHistories.stream()
                .collect(Collectors.groupingBy(OrderHistory::getUuid));

        model.addAttribute("address", Formatter.formatAddressToShow(user.getAddress()));

        model.addAttribute("previousOrders", groupedHistories.values());
        model.addAttribute("user", user);
        model.addAttribute("userDTO", new UserDTO());

        return "UserPage";
    }

    @PutMapping
    public String editUserPage(@PathVariable long id, @ModelAttribute("userDTO") UserDTO userDTO) {
        User user = userDTO.convertToUser(userService.getById(id));
        user.setId(id);
        userService.update(user);

        return "redirect:/user/" + id;
    }
}

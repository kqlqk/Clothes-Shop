package me.kqlqk.shop.controller;

import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final OrderService orderService;

    @Autowired
    public AdminController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String getAdminPage() {
        return "admin/AdminPage";
    }

    @GetMapping("/orders")
    public String getAdminOrdersPage(Model model) {
        List<Order> unrealisedOrders = new ArrayList<>();
        List<Order> realisedOrders = new ArrayList<>();

        orderService.getAll().forEach(e -> {
            if (e.isRealised()) {
                realisedOrders.add(e);
            }
            else {
                unrealisedOrders.add(e);
            }
        });

        Map<Integer, List<Order>> groupedUnrealisedOrders = unrealisedOrders.stream()
                .collect(Collectors.groupingBy(Order::getUuid));
        Map<Integer, List<Order>> groupedRealisedOrders = realisedOrders.stream()
                .collect(Collectors.groupingBy(Order::getUuid));


        model.addAttribute("unrealisedOrders", groupedUnrealisedOrders.values());
        model.addAttribute("realisedOrders", groupedRealisedOrders.values());

        return "admin/AdminOrderPage";
    }
}

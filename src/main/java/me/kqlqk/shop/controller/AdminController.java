package me.kqlqk.shop.controller;

import me.kqlqk.shop.dto.ProductDTO;
import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.service.OrderService;
import me.kqlqk.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final OrderService orderService;
    private final ProductService productService;

    @Autowired
    public AdminController(OrderService orderService, ProductService productService) {
        this.orderService = orderService;
        this.productService = productService;
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

    @GetMapping("/product")
    public String getAdminProductPage(Model model) {

        model.addAttribute("products", productService.getAll());
        return "admin/AdminProductPage";
    }

    @GetMapping("/product/edit/{id}")
    public String getAdminEditProductPage(@PathVariable long id, Model model) {
        Product product = productService.getById(id);

        model.addAttribute("id", id);
        model.addAttribute("productDTO", new ProductDTO());
        model.addAttribute("sizes", product.getSizes());
        model.addAttribute("colors", product.getColors());
        return "admin/AdminEditProductPage";
    }

    @PutMapping("/product/edit/{id}")
    public String editProduct(@PathVariable long id, @ModelAttribute("productDTO") ProductDTO productDTO) {
        Product product = productService.getById(id);
        product.setName(productDTO.getName().isBlank() ? product.getName() : productDTO.getName());
        product.setPrice(productDTO.getPrice() == 0 ? product.getPrice() : productDTO.getPrice());
        product.setDiscount(productDTO.getDiscount() == 0 ? product.getDiscount() : productDTO.getDiscount());
        product.setDescription(productDTO.getDescription().isBlank() ? product.getDescription() : productDTO.getDescription());
        product.setPath(productDTO.getPath().isBlank() ? product.getPath() : productDTO.getPath());

        productService.update(product);

        return "redirect:/admin/product";
    }
}

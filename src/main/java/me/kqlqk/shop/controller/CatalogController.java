package me.kqlqk.shop.controller;

import me.kqlqk.shop.model.Product;
import me.kqlqk.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/catalog")
public class CatalogController {
    private final ProductService productService;

    @Autowired
    public CatalogController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public String getPageWithAllProducts(Model model) {
        List<Product> products = productService.getAll();

        model.addAttribute("products", products);

        return "catalog/CatalogPage";
    }
}

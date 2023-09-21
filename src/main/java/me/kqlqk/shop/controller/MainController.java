package me.kqlqk.shop.controller;

import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.service.ProductService;
import me.kqlqk.shop.util.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {
    private final SearchUtil searchUtil;
    private final ProductService productService;

    @Autowired
    public MainController(SearchUtil searchUtil, ProductService productService) {
        this.searchUtil = searchUtil;
        this.productService = productService;
    }

    @GetMapping
    public String getMainPage(Model model) {
        List<Product> saleProducts = productService.getSales();
        List<Product> newProducts = productService.getLastProducts(100);
        List<Integer> saleProductsDiscounts = new ArrayList<>();
        List<Integer> newProductsDiscounts = new ArrayList<>();

        saleProducts.forEach(e -> saleProductsDiscounts.add((int) (e.getPrice() - (e.getPrice() / 100.0 * e.getDiscount()))));
        newProducts.forEach(e -> newProductsDiscounts.add((int) (e.getPrice() - (e.getPrice() / 100.0 * e.getDiscount()))));

        model.addAttribute("saleProducts", saleProducts);
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("saleProductsDiscounts", saleProductsDiscounts);
        model.addAttribute("newProductsDiscounts", newProductsDiscounts);

        return "MainPage";
    }

    @GetMapping("/search")
    public String getSearchPage(@RequestParam String search, Model model) {
        if (!search.isEmpty()) {
            List<Product> products = searchUtil.sortProductsByScore(searchUtil.getProductsWithScore(search));
            List<Integer> productsDiscounts = new ArrayList<>();

            products.forEach(e -> productsDiscounts.add((int) (e.getPrice() - (e.getPrice() / 100.0 * e.getDiscount()))));

            model.addAttribute("products", products);
            model.addAttribute("productsDiscounts", productsDiscounts);
        }

        return "catalog/SearchPage";
    }
}

package me.kqlqk.shop.controller;

import me.kqlqk.shop.util.SearchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class MainController {
    private final SearchUtil searchUtil;

    @Autowired
    public MainController(SearchUtil searchUtil) {
        this.searchUtil = searchUtil;
    }

    @GetMapping
    public String getMainPage() {
        return "MainPage";
    }

    @GetMapping("/search")
    public String search(@RequestParam String search, Model model) {
        if (!search.isEmpty()) {
            model.addAttribute("products",
                    searchUtil.sortProductsByScore(searchUtil.getProductsWithScore(search)));
        }

        return "SearchPage";
    }
}

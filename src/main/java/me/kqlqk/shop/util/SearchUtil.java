package me.kqlqk.shop.util;

import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SearchUtil {
    private final ProductService productService;

    @Autowired
    public SearchUtil(ProductService productService) {
        this.productService = productService;
    }

    public Map<Product, Integer> getProductsWithScore(String toSearch) {
        List<Product> allProducts = productService.getAll();
        String[] wordsToSearch = toSearch.split(" ");
        Map<Product, Integer> productScores = new HashMap<>();

        for (Product product : allProducts) {
            int score = calculateScore(product, wordsToSearch);
            if (score > 0) {
                productScores.put(product, score);
            }
        }

        return productScores;
    }

    public List<Product> sortProductsByScore(Map<Product, Integer> productScores) {
        return productScores
                .entrySet()
                .stream()
                .sorted(Map.Entry.<Product, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    private int calculateScore(Product product, String[] searchWords) {
        String productName = product.getName().toLowerCase();
        int score = 0;

        for (String word : searchWords) {
            if (productName.equals(word)) {
                score += 2;
            }
            else if (productName.contains(word)) {
                score += 1;
            }
        }

        return score;
    }
}

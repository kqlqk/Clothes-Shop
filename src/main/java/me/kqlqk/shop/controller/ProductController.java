package me.kqlqk.shop.controller;

import me.kqlqk.shop.dto.OrderDTO;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalog")
public class ProductController {
    @Value("${folder.images}")
    private String IMAGES_FOLDER;

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    public String getProductPage(@PathVariable long id, Model model,
                                 @RequestParam(value = "errors", required = false) String[] errors) throws IOException {
        Product product = productService.getById(id);

        List<String> fileNames = Files.walk(Paths.get(IMAGES_FOLDER + "/" + product.getPath()))
                .map(Path::getFileName)
                .map(Path::toString)
                .filter(e -> e.endsWith(".png"))
                .collect(Collectors.toList());

        model.addAttribute("product", product);
        model.addAttribute("files", fileNames);
        model.addAttribute("orderDTO", new OrderDTO());

        if (errors != null) {
            for (String s : errors) {
                model.addAttribute(s, s);
            }
        }

        return "catalog/ProductPage";
    }

}

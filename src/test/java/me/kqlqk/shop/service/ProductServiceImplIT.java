package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.ProductExistsException;
import me.kqlqk.shop.exception.ProductNotFoundException;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;
import me.kqlqk.shop.model.product.Color;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.model.product.Size;
import me.kqlqk.shop.repository.ProductRepository;
import me.kqlqk.shop.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class ProductServiceImplIT {
    @Autowired
    private ProductServiceImpl productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void add_shouldAddProductToDB() {
        int oldSize = productRepository.findAll().size();

        List<Color> colors = new ArrayList<>();
        List<Size> sizes = new ArrayList<>();

        Product product = new Product(
                "Black oversize t-shirt", 10, 0, "...", colors, sizes, "/tshirts/3");

        Size size = new Size(Sizes.XS);
        size.setProduct(product);
        sizes.add(size);

        Color color = new Color(Colors.WHITE);
        color.setProduct(product);
        colors.add(color);

        productService.add(product);

        int newSize = productRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        Product product = productService.getById(1);
        product.setName("new name");
        product.setPath("/new/path");
        Product productExistedById = product;
        assertThrows(ProductExistsException.class, () -> productService.add(productExistedById));

        product = productService.getById(1);
        product.setId(0);
        product.setPath("/new/path");
        Product productExistedByName = product;
        assertThrows(ProductExistsException.class, () -> productService.add(productExistedByName));

        product = productService.getById(1);
        product.setId(0);
        product.setName("newName");
        Product productExistedByPath = product;
        assertThrows(ProductExistsException.class, () -> productService.add(productExistedByPath));
    }

    @Test
    public void update_shouldUpdateProductInDB() {
        Product product = productService.getById(1);
        product.setDescription("new description");

        productService.update(product);

        assertThat(productService.getById(1).getDescription()).isEqualTo(product.getDescription());
    }

    @Test
    public void update_shouldThrowException() {
        Product product = new Product(
                "Black oversize t-shirt", 10, 0, "...", null, null, "/tshirts/3");
        product.setId(99);
        assertThrows(ProductNotFoundException.class, () -> productService.update(product));
    }
}

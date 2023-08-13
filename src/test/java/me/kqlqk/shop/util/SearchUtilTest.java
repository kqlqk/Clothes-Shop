package me.kqlqk.shop.util;

import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchUtilTest {

    @InjectMocks
    private SearchUtil searchUtil;

    @Mock
    private ProductService productService;

    @Test
    public void getProductsWithScore_shouldReturnProductsWithScore() {
        when(productService.getAll()).thenReturn(List.of(
                new Product("blue tshirt", 1, 0, null, null, null, null),
                new Product("white tshirt", 1, 0, null, null, null, null),
                new Product("red hat", 1, 0, null, null, null, null),
                new Product("blue jeans", 1, 0, null, null, null, null)
        ));

        Map<Product, Integer> productScores = searchUtil.getProductsWithScore("blue tshirt");

        assertEquals(3, productScores.size());
    }

    @Test
    public void sortProductsByScore_shouldReturnSortedProductsByScore() {
        Map<Product, Integer> productScores = Map.of(
                new Product("blue tshirt", 1, 0, null, null, null, null), 3,
                new Product("white tshirt", 1, 0, null, null, null, null), 2,
                new Product("red hat", 1, 0, null, null, null, null), 4,
                new Product("blue jeans", 1, 0, null, null, null, null), 1
        );

        List<Product> sortedProducts = searchUtil.sortProductsByScore(productScores);

        assertEquals(4, sortedProducts.size());
        assertEquals("red hat", sortedProducts.get(0).getName());
        assertEquals("blue tshirt", sortedProducts.get(1).getName());
        assertEquals("white tshirt", sortedProducts.get(2).getName());
        assertEquals("blue jeans", sortedProducts.get(3).getName());
    }
}

package me.kqlqk.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.kqlqk.shop.model.Product;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDTO {
    private Product product;
    private ProductBuyingDTO productBuyingDTO;
}

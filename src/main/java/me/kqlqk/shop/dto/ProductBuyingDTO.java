package me.kqlqk.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductBuyingDTO {
    private int id;
    private long productId;
    private Colors color;
    private Sizes size;
}

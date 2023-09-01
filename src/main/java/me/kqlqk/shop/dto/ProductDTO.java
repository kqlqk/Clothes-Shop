package me.kqlqk.shop.dto;

import lombok.Data;
import me.kqlqk.shop.model.product.Color;
import me.kqlqk.shop.model.product.Size;

import java.util.List;

@Data
public class ProductDTO {
    private long id;
    private String name;
    private int price;
    private int discount;
    private String description;
    private List<Color> colors;
    private List<Size> sizes;
    private String path;
}

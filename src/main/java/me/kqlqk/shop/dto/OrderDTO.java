package me.kqlqk.shop.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.kqlqk.shop.model.Card;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.model.enums.Sizes;
import me.kqlqk.shop.model.product.Color;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.model.product.Size;
import me.kqlqk.shop.model.user.User;

@Data
@NoArgsConstructor
public class OrderDTO {
    private long id;
    private Product product;
    private Color color;
    private Size size;
    private User user;
    private AddressDTO addressDTO;
    private boolean authorized;
    private long productId;
    @NotNull
    private Colors colorName;
    @NotNull
    private Sizes sizeName;

    public OrderDTO(long id, Product product, Color color, Size size, User user, boolean authorized) {
        this.id = id;
        this.product = product;
        this.color = color;
        this.size = size;
        this.user = user;
        this.authorized = authorized;
    }

    public OrderDTO(long id, long productId, Colors colorName, Sizes sizeName, boolean authorized) {
        this.id = id;
        this.productId = productId;
        this.colorName = colorName;
        this.sizeName = sizeName;
        this.authorized = authorized;
    }

    public static OrderDTO convertToOrderDTO(Card card) {
        return new OrderDTO(card.getId(), card.getProduct(), card.getColor(), card.getSize(), card.getUser(), true);
    }
}

package me.kqlqk.shop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.kqlqk.shop.model.product.Color;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.model.product.Size;
import me.kqlqk.shop.model.user.User;

@Entity
@Table(name = "shopping_card")
@Getter
@Setter
@NoArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "color_id", referencedColumnName = "id")
    private Color color;

    @ManyToOne
    @JoinColumn(name = "size_id", referencedColumnName = "id")
    private Size size;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private User user;

    public Card(Color color, Size size, Product product, User user) {
        this.color = color;
        this.size = size;
        this.product = product;
        this.user = user;
    }
}

package me.kqlqk.shop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.kqlqk.shop.model.product.Color;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.model.product.Size;
import me.kqlqk.shop.model.user.Address;
import me.kqlqk.shop.model.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "buying_order")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private int id;

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

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

    @Column(name = "delivered_date")
    private LocalDateTime deliveredDate;

    @Column(name = "uuid", nullable = false)
    private int uuid; // TODO rename

    @Column(name = "realised", nullable = false)
    private boolean realised;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    public Order(Color color, Size size, Product product, User user, LocalDateTime createDate, LocalDateTime deliveredDate, int uuid, boolean realised) {
        this.color = color;
        this.size = size;
        this.product = product;
        this.user = user;
        this.createDate = createDate;
        this.deliveredDate = deliveredDate;
        this.uuid = uuid;
        this.realised = realised;
    }

    public Order(Color color, Size size, Product product, User user, LocalDateTime createDate, LocalDateTime deliveredDate, int uuid, boolean realised, Address address) {
        this.color = color;
        this.size = size;
        this.product = product;
        this.user = user;
        this.createDate = createDate;
        this.deliveredDate = deliveredDate;
        this.uuid = uuid;
        this.realised = realised;
        this.address = address;
    }
}

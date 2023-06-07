package me.kqlqk.shop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private long id;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "address")
    private String address;

    @OneToMany
    @JoinTable(name = "last_order",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<Order> lastOrders;

    @OneToMany
    @JoinTable(name = "current_order",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "order_id"))
    private List<Order> currentOrder;

    public User(String name, String address, List<Order> lastOrders, List<Order> currentOrder) {
        this.name = name;
        this.address = address;
        this.lastOrders = lastOrders;
        this.currentOrder = currentOrder;
    }
}

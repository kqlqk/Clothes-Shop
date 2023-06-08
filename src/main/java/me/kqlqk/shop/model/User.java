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
    @JoinColumn(name = "user_id")
    private List<OrderHistory> OrderHistory;


    public User(String name, String address, List<OrderHistory> OrderHistory) {
        this.name = name;
        this.address = address;
        this.OrderHistory = OrderHistory;
    }
}

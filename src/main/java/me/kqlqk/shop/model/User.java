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

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "password", nullable = false, length = 50)
    private String password; // TODO: 22/06/2023 Change to char[]

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private List<OrderHistory> orderHistory;


    public User(String email, String name, String password, String address, List<OrderHistory> OrderHistory) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.address = address;
        this.orderHistory = OrderHistory;
    }
}

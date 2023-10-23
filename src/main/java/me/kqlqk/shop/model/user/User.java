package me.kqlqk.shop.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.kqlqk.shop.model.Card;

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

    @Column(name = "password", nullable = false, length = 50)
    private char[] password;

    @OneToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    private Address address;

    @JsonIgnore
    @OneToMany
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    private List<Card> card;

    public User(String email, String name, char[] password, Address address) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.address = address;
    }
}

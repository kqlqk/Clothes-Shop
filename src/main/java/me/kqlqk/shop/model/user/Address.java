package me.kqlqk.shop.model.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
@EqualsAndHashCode(exclude = "id")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private long id;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "street", nullable = false)
    private String street;

    @Column(name = "house", nullable = false)
    private String house;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    public Address(String country, String city, String street, String house, String postalCode) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
        this.postalCode = postalCode;
    }
}

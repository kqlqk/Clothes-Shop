package me.kqlqk.shop.model.user;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "address")
@Data
@NoArgsConstructor
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

    @Column(name = "flat", nullable = false)
    private String flat;

    @Column(name = "postal_code", nullable = false)
    private String postalCode;

    public Address(String country, String city, String street, String house) {
        this.country = country;
        this.city = city;
        this.street = street;
        this.house = house;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Address address = (Address) o;
        return Objects.equals(country, address.country)
                && Objects.equals(city, address.city)
                && Objects.equals(street, address.street)
                && Objects.equals(house, address.house);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, city, street, house);
    }
}

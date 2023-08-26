package me.kqlqk.shop.model.product;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.kqlqk.shop.model.enums.Colors;

@Entity
@Table(name = "color")
@Getter
@Setter
@NoArgsConstructor
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", nullable = false, length = 50, unique = true)
    private Colors name;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public Color(Colors name, Product product) {
        this.name = name;
        this.product = product;
    }

    public Color(Colors name) {
        this.name = name;
    }
}

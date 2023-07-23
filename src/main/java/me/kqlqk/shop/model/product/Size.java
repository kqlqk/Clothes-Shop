package me.kqlqk.shop.model.product;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.kqlqk.shop.model.enums.Sizes;

@Entity
@Table(name = "size")
@Getter
@Setter
@NoArgsConstructor
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false, length = 100, unique = true)
    private Sizes name;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "id")
    private Product product;

    public Size(Sizes name) {
        this.name = name;
    }

    public Size(Sizes name, Product product) {
        this.name = name;
        this.product = product;
    }
}
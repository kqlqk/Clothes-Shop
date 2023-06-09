package me.kqlqk.shop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.kqlqk.shop.model.enums.Sizes;

import java.util.List;

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
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private Sizes name;

    @ManyToMany(mappedBy = "sizes")
    private List<Product> products;

    public Size(Sizes name) {
        this.name = name;
    }
}
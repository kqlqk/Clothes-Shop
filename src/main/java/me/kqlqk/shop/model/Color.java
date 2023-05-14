package me.kqlqk.shop.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.kqlqk.shop.model.enums.Colors;

import java.util.List;

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
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private Colors name;

    @ManyToMany(mappedBy = "colors")
    private List<Product> products;

    public Color(Colors name) {
        this.name = name;
    }
}

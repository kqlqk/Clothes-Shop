package me.kqlqk.shop.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "tag", catalog = "product", schema = "public")
@Data
@NoArgsConstructor
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name", nullable = false, length = 100)
    private Tags name;

    @ManyToMany(mappedBy = "tags")
    private List<Product> products;

    public Tag(Tags name) {
        this.name = name;
    }
}

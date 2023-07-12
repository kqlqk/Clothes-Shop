package me.kqlqk.shop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, updatable = false)
    private long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "order_date", nullable = false)
    private LocalDateTime date;

    @Column(name = "uuid", nullable = false)
    private long uuid;

    @Column(name = "released", nullable = false)
    private boolean released;

    public OrderHistory(User user, Order order, LocalDateTime date, long uuid, boolean released) {
        this.user = user;
        this.order = order;
        this.date = date;
        this.uuid = uuid;
        this.released = released;
    }
}

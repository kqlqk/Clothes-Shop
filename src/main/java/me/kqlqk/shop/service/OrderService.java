package me.kqlqk.shop.service;

import me.kqlqk.shop.model.Order;
import org.springframework.stereotype.Service;

@Service
public interface OrderService {
    Order getById(long id);

    void add(Order order);

    void update(Order order);
}

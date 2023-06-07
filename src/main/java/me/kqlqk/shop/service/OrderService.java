package me.kqlqk.shop.service;

import me.kqlqk.shop.model.Order;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    List<Order> getCurrentOrder(long userId);

    List<Order> getLastOrders(long userId);

    void add(Order order);

    void update(Order order);
}

package me.kqlqk.shop.service;

import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OrderService {
    Order getById(int id);

    List<Order> getByUser(User user);

    List<Order> getByUserAndRealised(User user, boolean realised);

    List<Order> getAll();

    void add(Order order);

    void update(Order order);
}

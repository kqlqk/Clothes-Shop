package me.kqlqk.shop.service.impl;

import lombok.NonNull;
import me.kqlqk.shop.exception.OrderExistsException;
import me.kqlqk.shop.exception.OrderNotFoundException;
import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.repository.OrderRepository;
import me.kqlqk.shop.service.OrderService;
import me.kqlqk.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, UserService userService) {
        this.orderRepository = orderRepository;
        this.userService = userService;
    }

    @Override
    public Order getCurrentOrder(long userId) {
        User user = userService.getById(userId);

        if (user.getCurrentOrder() == null) {
            throw new OrderNotFoundException("Current order for user with userId = " + userId + " not found");
        }

        return user.getCurrentOrder();
    }

    @Override
    public List<Order> getLastOrders(long userId) {
        User user = userService.getById(userId);

        if (user.getLastOrders() == null) {
            throw new OrderNotFoundException("Last orders for user with userId = " + userId + " not found");
        }

        return user.getLastOrders();
    }

    @Override
    public void add(@NonNull Order order) {
        if (orderRepository.existsById(order.getId())) {
            throw new OrderExistsException("Order with id = " + order.getId() + " exists");
        }

        orderRepository.save(order);
    }

    @Override
    public void update(@NonNull Order order) {
        if (!orderRepository.existsById(order.getId())) {
            throw new OrderNotFoundException("Order with id = " + order.getId() + " exists");
        }

        orderRepository.save(order);
    }
}

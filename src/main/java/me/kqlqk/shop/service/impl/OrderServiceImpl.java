package me.kqlqk.shop.service.impl;

import lombok.NonNull;
import me.kqlqk.shop.exception.OrderExistsException;
import me.kqlqk.shop.exception.OrderNotFoundException;
import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.user.User;
import me.kqlqk.shop.repository.OrderRepository;
import me.kqlqk.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public Order getById(long id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException("Order with id = " + id + " not found"));
    }

    @Override
    public List<Order> getByUser(@NonNull User user) {
        List<Order> orders = orderRepository.findByUser(user);

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("Orders for user with userId = " + user.getId() + " not found");
        }

        return orders;
    }

    @Override
    public List<Order> getByUserAndRealised(@NonNull User user, boolean realised) {
        List<Order> orders = orderRepository.findByUserAndRealisedOrderByCreateDate(user, realised);

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("Orders for user with userId = " + user.getId() + " not found");
        }

        return orders;
    }

    @Override
    public List<Order> getAll() {
        List<Order> orders = orderRepository.findAll();

        if (orders.isEmpty()) {
            throw new OrderNotFoundException("Orders not found");
        }

        return orders;
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
            throw new OrderNotFoundException("Order with id = " + order.getId() + " not found");
        }
        // TODO: 22/07/2023 add checking like in userServiceImpl

        orderRepository.save(order);
    }
}

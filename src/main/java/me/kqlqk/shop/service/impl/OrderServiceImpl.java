package me.kqlqk.shop.service.impl;

import lombok.NonNull;
import me.kqlqk.shop.exception.OrderExistsException;
import me.kqlqk.shop.exception.OrderNotFoundException;
import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.repository.OrderRepository;
import me.kqlqk.shop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        orderRepository.save(order);
    }
}

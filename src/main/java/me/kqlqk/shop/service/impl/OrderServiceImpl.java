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

        Order orderDb = getById(order.getId());

        if (order.getColor() == null) {
            order.setColor(orderDb.getColor());
        }

        if (order.getSize() == null) {
            order.setSize(orderDb.getSize());
        }

        if (order.getProduct() == null) {
            order.setProduct(orderDb.getProduct());
        }

        if (order.getUser() == null) {
            order.setUser(orderDb.getUser());
        }

        if (order.getCreateDate() == null) {
            order.setCreateDate(orderDb.getCreateDate());
        }

        if (order.getDeliveredDate() == null) {
            order.setDeliveredDate(orderDb.getDeliveredDate());
        }

        if (order.getUuid() == 0) {
            order.setUuid(orderDb.getUuid());
        }

        if (order.getAddress() == null) {
            order.setAddress(orderDb.getAddress());
        }

        orderRepository.save(order);
    }
}

package me.kqlqk.shop.service.impl;

import me.kqlqk.shop.exception.OrderHistoryExistsException;
import me.kqlqk.shop.exception.OrderHistoryNotFoundException;
import me.kqlqk.shop.model.OrderHistory;
import me.kqlqk.shop.repository.OrderHistoryRepository;
import me.kqlqk.shop.service.OrderHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {
    private final OrderHistoryRepository orderHistoryRepository;

    @Autowired
    public OrderHistoryServiceImpl(OrderHistoryRepository orderHistoryRepository) {
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    public OrderHistory getById(long id) {
        return orderHistoryRepository.findById(id)
                .orElseThrow(() -> new OrderHistoryNotFoundException("Order history with id = " + id + " not found"));
    }

    @Override
    public void add(OrderHistory orderHistory) {
        if (orderHistoryRepository.existsById(orderHistory.getId())) {
            throw new OrderHistoryExistsException("Order history object with id = " + orderHistory.getId() + " exists");
        }

        orderHistoryRepository.save(orderHistory);
    }

    @Override
    public void update(OrderHistory orderHistory) {
        if (!orderHistoryRepository.existsById(orderHistory.getId())) {
            throw new OrderHistoryNotFoundException("Order history object with id = " + orderHistory.getId() + " not found");
        }

        orderHistoryRepository.save(orderHistory);
    }
}

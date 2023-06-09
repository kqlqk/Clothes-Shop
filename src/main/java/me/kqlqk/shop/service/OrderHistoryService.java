package me.kqlqk.shop.service;

import me.kqlqk.shop.model.OrderHistory;
import org.springframework.stereotype.Service;

@Service
public interface OrderHistoryService {
    OrderHistory getById(long id);

    void add(OrderHistory orderHistory);

    void update(OrderHistory orderHistory);
}

package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.OrderHistoryExistsException;
import me.kqlqk.shop.exception.OrderHistoryNotFoundException;
import me.kqlqk.shop.model.OrderHistory;
import me.kqlqk.shop.repository.OrderHistoryRepository;
import me.kqlqk.shop.service.impl.OrderHistoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class OrderHistoryServiceImplIT {
    @Autowired
    private OrderHistoryServiceImpl orderHistoryService;

    @Autowired
    private OrderHistoryRepository orderHistoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Test
    public void add_shouldAddOrderHistoryToDB() {
        int oldSize = orderHistoryRepository.findAll().size();

        OrderHistory orderHistory = new OrderHistory(userService.getById(1), orderService.getById(1), LocalDateTime.now(), 1, false);

        orderHistoryService.add(orderHistory);

        int newSize = orderHistoryRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        OrderHistory orderHistory = orderHistoryService.getById(1);
        orderHistory.setUser(userService.getById(2));

        assertThrows(OrderHistoryExistsException.class, () -> orderHistoryService.add(orderHistory));
    }

    @Test
    public void update_shouldUpdateUserInDB() {
        OrderHistory orderHistory = orderHistoryService.getById(1);
        orderHistory.setReleased(false);

        orderHistoryService.update(orderHistory);

        assertThat(orderHistoryService.getById(1).isReleased()).isEqualTo(orderHistory.isReleased());
    }

    @Test
    public void update_shouldThrowException() {
        OrderHistory orderHistory = new OrderHistory(userService.getById(1), orderService.getById(1), LocalDateTime.now(), 1, false);
        assertThrows(OrderHistoryNotFoundException.class, () -> orderHistoryService.update(orderHistory));
    }
}

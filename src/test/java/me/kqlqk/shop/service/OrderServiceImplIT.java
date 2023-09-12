package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.OrderExistsException;
import me.kqlqk.shop.exception.OrderNotFoundException;
import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.repository.OrderRepository;
import me.kqlqk.shop.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class OrderServiceImplIT {
    @Autowired
    private OrderServiceImpl orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductService productService;

    @Test
    @Transactional
    public void add_shouldAddOrderToDB() {
        int oldSize = orderRepository.findAll().size();

        Product product = productService.getById(1);

        Order order = new Order(product.getColors().get(0),
                product.getSizes().get(0),
                product,
                null,
                LocalDateTime.now(),
                null,
                1,
                false);

        orderService.add(order);

        int newSize = orderRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        Order order = orderService.getById(1);
        assertThrows(OrderExistsException.class, () -> orderService.add(order));
    }

    @Test
    @Transactional
    public void update_shouldUpdateOrderInDB() {
        Order order = orderService.getById(1);
        order.setColor(productService.getById(1).getColors().get(1));

        orderService.update(order);

        assertThat(orderService.getById(1).getColor().getName()).isEqualTo(order.getColor().getName());
    }

    @Test
    @Transactional
    public void update_shouldThrowException() {
        Product product = productService.getById(1);

        Order order = new Order(product.getColors().get(0),
                product.getSizes().get(0),
                product,
                null,
                LocalDateTime.now(),
                null,
                1,
                false);
        order.setId(99);


        assertThrows(OrderNotFoundException.class, () -> orderService.update(order));
    }
}

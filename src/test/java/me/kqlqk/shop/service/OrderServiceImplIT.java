package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.OrderExistsException;
import me.kqlqk.shop.exception.OrderNotFoundException;
import me.kqlqk.shop.model.Color;
import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.Product;
import me.kqlqk.shop.model.Size;
import me.kqlqk.shop.repository.OrderRepository;
import me.kqlqk.shop.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Autowired
    private ColorService colorService;

    @Autowired
    private SizeService sizeService;

    @Test
    public void add_shouldAddUserToDB() {
        int oldSize = orderRepository.findAll().size();

        Color color = colorService.getById(1);
        Size size = sizeService.getById(1);
        Product product = productService.getById(1);

        Order order = new Order(color, size, product);

        orderService.add(order);

        int newSize = orderRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        Order order = orderRepository.findById(1l).get();

        assertThrows(OrderExistsException.class, () -> orderService.add(order));
    }

    @Test
    public void update_shouldUpdateUserInDB() {
        Color color = colorService.getById(2);
        Order order = orderRepository.findById(1l).get();
        order.setColor(color);


        orderService.update(order);

        assertThat(orderRepository.findById(1l).get().getColor().getName()).isEqualTo(order.getColor().getName());
    }

    @Test
    public void update_shouldThrowException() {
        Order order = new Order(null, null, null);
        assertThrows(OrderNotFoundException.class, () -> orderService.update(order));
    }
}

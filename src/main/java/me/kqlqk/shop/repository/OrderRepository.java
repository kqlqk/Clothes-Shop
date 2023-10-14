package me.kqlqk.shop.repository;

import me.kqlqk.shop.model.Order;
import me.kqlqk.shop.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUser(User user);

    List<Order> findByUserAndRealisedOrderByCreateDate(User user, boolean realised);
}

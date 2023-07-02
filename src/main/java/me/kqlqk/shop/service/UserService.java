package me.kqlqk.shop.service;

import me.kqlqk.shop.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User getById(long id);

    User getByEmail(String email);

    void add(User user);

    void update(User user);

    boolean existsByEmail(String email);

    boolean existsById(long id);
}

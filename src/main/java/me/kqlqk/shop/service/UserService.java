package me.kqlqk.shop.service;

import me.kqlqk.shop.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User getById(long id);

    void add(User user);

    void update(User user);
}

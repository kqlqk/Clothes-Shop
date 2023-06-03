package me.kqlqk.shop.service.impl;

import lombok.NonNull;
import me.kqlqk.shop.exception.UserExistsException;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.repository.UserRepository;
import me.kqlqk.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id = " + id + " not found"));
    }

    @Override
    public void add(@NonNull User user) {
        if (userRepository.existsById(user.getId())) {
            throw new UserExistsException("User with id = " + user.getId() + " exists");
        }

        userRepository.save(user);
    }

    @Override
    public void update(@NonNull User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("User with id = " + user.getId() + " not found");
        }

        userRepository.save(user);
    }
}

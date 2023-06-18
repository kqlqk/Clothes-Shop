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
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserExistsException("User with email = " + user.getEmail() + " exists");
        }

        userRepository.save(user);
    }

    @Override
    public void update(@NonNull User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException("User with id = " + user.getId() + " not found");
        }

        User userDb = getById(user.getId());

        if (!userDb.getEmail().equals(user.getEmail()) && userRepository.existsByEmail(user.getEmail())) {
            throw new UserExistsException("User with email = " + user.getEmail() + " exists");
        }


        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(userDb.getEmail());
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(userDb.getName());
        }

        if (user.getAddress() == null || user.getAddress().isBlank()) {
            user.setAddress(userDb.getAddress());
        }

        if (user.getOrderHistory() == null || user.getOrderHistory().isEmpty()) {
            user.setOrderHistory(userDb.getOrderHistory());
        }

        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}

package me.kqlqk.shop.service.impl;

import lombok.NonNull;
import me.kqlqk.shop.exception.UserExistsException;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.model.user.User;
import me.kqlqk.shop.repository.UserRepository;
import me.kqlqk.shop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.CharBuffer;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setPasswordEncoder(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User getById(long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User with id = " + id + " not found"));
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User with email = " + email + " not found"));
    }

    @Override
    public void add(@NonNull User user) {
        if (userRepository.existsById(user.getId())) {
            throw new UserExistsException("User with id = " + user.getId() + " exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserExistsException("User with email = " + user.getEmail() + " exists");
        }
        if (user.getAddress() != null && userRepository.existsByAddress(user.getAddress())) {
            throw new UserExistsException("User with address = " + user.getAddress() + " exists");
        }

        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(user.getPassword())).toCharArray());

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

        if (user.getAddress() == null) {
            user.setAddress(userDb.getAddress());
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            user.setEmail(userDb.getEmail());
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(userDb.getName());
        }

        if (user.getCard() == null) {
            user.setCard(userDb.getCard());
        }

        if (user.getPassword() == null) {
            user.setPassword(userDb.getPassword());
        }
        else {
            user.setPassword(passwordEncoder.encode(CharBuffer.wrap(user.getPassword())).toCharArray());
        }

        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(long id) {
        return userRepository.existsById(id);
    }
}

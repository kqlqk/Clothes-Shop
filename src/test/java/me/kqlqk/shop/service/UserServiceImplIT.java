package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.UserExistsException;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.model.User;
import me.kqlqk.shop.repository.UserRepository;
import me.kqlqk.shop.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class UserServiceImplIT {
    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void add_shouldAddUserToDB() {
        int oldSize = userRepository.findAll().size();

        User user = new User("test", "test2", null);

        userService.add(user);

        int newSize = userRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        User user = userService.getById(1);
        user.setName("new name");

        assertThrows(UserExistsException.class, () -> userService.add(user));
    }

    @Test
    public void update_shouldUpdateUserInDB() {
        User user = userService.getById(1);
        user.setAddress("new address");

        userService.update(user);

        assertThat(userService.getById(1).getAddress()).isEqualTo(user.getAddress());
    }

    @Test
    public void update_shouldThrowException() {
        User user = new User("test1", "test2", null);
        assertThrows(UserNotFoundException.class, () -> userService.update(user));
    }
}

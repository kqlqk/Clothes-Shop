package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.UserExistsException;
import me.kqlqk.shop.exception.UserNotFoundException;
import me.kqlqk.shop.model.user.Address;
import me.kqlqk.shop.model.user.User;
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

        User user = new User("test@test.com", "user", "Password1", null);

        userService.add(user);

        int newSize = userRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        User user = userService.getById(1);
        user.setName("new name");
        user.setAddress(null);

        User userExistedById = user;
        assertThrows(UserExistsException.class, () -> userService.add(userExistedById));

        user = new User("email@email.com", "aaa", "password", null);
        User userExistedByEmail = user;
        assertThrows(UserExistsException.class, () -> userService.add(userExistedByEmail));

        user = new User("new@email.com", "aaa", "password", userService.getById(1).getAddress());
        User userExistedByAddress = user;
        assertThrows(UserExistsException.class, () -> userService.add(userExistedByAddress));
    }

    @Test
    public void update_shouldUpdateUserInDB() {
        User user = userService.getById(1);
        user.setAddress(new Address("USA", "Chicago", "Chicago avenue", "1/3", "0000"));

        userService.update(user);

        assertThat(userService.getById(1).getAddress()).isEqualTo(user.getAddress());
    }

    @Test
    public void update_shouldThrowException() {
        User user = new User("new@email.com", "a", "Password1", null);
        user.setId(99);
        User userWithExistedId = user;
        assertThrows(UserNotFoundException.class, () -> userService.update(userWithExistedId));

        user = userService.getById(1);
        user.setEmail("email2@email.com");
        User userWithExistedEmail = user;
        assertThrows(UserExistsException.class, () -> userService.update(userWithExistedEmail));
    }
}

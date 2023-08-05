package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.CardExistsException;
import me.kqlqk.shop.exception.CardNotFoundException;
import me.kqlqk.shop.model.Card;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.repository.CardRepository;
import me.kqlqk.shop.service.impl.CardServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class CardServiceImplIT {
    @Autowired
    private CardServiceImpl cardService;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Test
    @Transactional
    public void add_shouldAddCardToDB() {
        int oldSize = cardRepository.findAll().size();

        Product product = productService.getById(1);
        Card card = new Card(product.getColors().get(0), product.getSizes().get(0), product, userService.getById(1));

        cardService.add(card);

        int newSize = cardRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        assertThrows(CardExistsException.class, () -> cardService.add(cardService.getByUser(userService.getById(1)).get(0)));
    }

    @Test
    @Transactional
    public void update_shouldUpdateProductInDB() {
        Card card = cardService.getByUser(userService.getById(1)).get(0);
        card.setSize(productService.getById(1).getSizes().get(1));

        cardService.update(card);

        assertThat(cardService.getByUser(userService.getById(1)).get(0).getSize().getName()).isEqualTo(card.getSize().getName());
    }

    @Test
    public void update_shouldThrowException() {
        Card card = cardService.getByUser(userService.getById(1)).get(0);
        card.setId(99);
        assertThrows(CardNotFoundException.class, () -> cardService.update(card));
    }

    @Test
    @Transactional
    public void remove_shouldRemoveProductInDB() {
        int oldSize = cardRepository.findAll().size();

        Card card = cardService.getByUser(userService.getById(1)).get(0);

        cardService.remove(card);

        int newSize = cardRepository.findAll().size();

        assertThat(oldSize - 1).isEqualTo(newSize);
    }

    @Test
    public void remove_shouldThrowException() {
        Card card = cardService.getByUser(userService.getById(1)).get(0);
        card.setId(99);
        assertThrows(CardNotFoundException.class, () -> cardService.remove(card));
    }
}

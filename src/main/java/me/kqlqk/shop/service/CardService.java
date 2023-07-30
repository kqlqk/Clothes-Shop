package me.kqlqk.shop.service;

import me.kqlqk.shop.model.Card;
import me.kqlqk.shop.model.user.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CardService {
    List<Card> getByUser(User user);

    void add(Card card);

    void update(Card card);

    void remove(Card card);
}

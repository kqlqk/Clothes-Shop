package me.kqlqk.shop.service.impl;

import lombok.NonNull;
import me.kqlqk.shop.exception.CardExistsException;
import me.kqlqk.shop.exception.CardNotFoundException;
import me.kqlqk.shop.model.Card;
import me.kqlqk.shop.model.user.User;
import me.kqlqk.shop.repository.CardRepository;
import me.kqlqk.shop.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public List<Card> getByUser(@NonNull User user) {
        List<Card> card = cardRepository.findByUser(user);

        if (card.isEmpty()) {
            throw new CardNotFoundException("Card with user, userId = " + user.getId() + " not found");
        }

        return card;
    }

    @Override
    public void add(Card card) {
        if (cardRepository.existsById(card.getId())) {
            throw new CardExistsException("Card with id = " + card.getId() + " exists");
        }

        cardRepository.save(card);
    }

    @Override
    public void update(Card card) {
        if (!cardRepository.existsById(card.getId())) {
            throw new CardNotFoundException("Card with id = " + card.getId() + " not found");
        }

        cardRepository.save(card);
    }

    @Override
    public void remove(Card card) {
        if (!cardRepository.existsById(card.getId())) {
            throw new CardNotFoundException("Card with id = " + card.getId() + " not found");
        }

        cardRepository.delete(card);
    }
}

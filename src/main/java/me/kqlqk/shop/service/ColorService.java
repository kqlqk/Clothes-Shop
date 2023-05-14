package me.kqlqk.shop.service;

import me.kqlqk.shop.model.Color;
import me.kqlqk.shop.model.enums.Colors;
import org.springframework.stereotype.Service;

@Service
public interface ColorService {
    Color getById(long id);

    Color getByName(Colors name);

    void add(Color color);

    void update(Color color);
}

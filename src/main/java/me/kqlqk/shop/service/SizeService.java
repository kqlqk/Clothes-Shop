package me.kqlqk.shop.service;

import me.kqlqk.shop.model.Size;
import me.kqlqk.shop.model.enums.Sizes;
import org.springframework.stereotype.Service;

@Service
public interface SizeService {
    Size getById(long id);

    Size getByName(Sizes name);

    void add(Size size);

    void update(Size size);
}

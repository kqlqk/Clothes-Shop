package me.kqlqk.shop.service;

import me.kqlqk.shop.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductService {
    Product getById(long id);

    Product getByName(String name);

    List<Product> getAll();

    void add(Product product);

    void update(Product product);
}

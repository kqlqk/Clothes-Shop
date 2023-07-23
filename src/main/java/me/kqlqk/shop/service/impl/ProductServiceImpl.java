package me.kqlqk.shop.service.impl;

import me.kqlqk.shop.exception.ProductExistsException;
import me.kqlqk.shop.exception.ProductNotFoundException;
import me.kqlqk.shop.model.product.Product;
import me.kqlqk.shop.repository.ProductRepository;
import me.kqlqk.shop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product getById(long id) {
        return productRepository.findById(id).orElseThrow(() ->
                new ProductNotFoundException("Product with id = " + id + " not found"));
    }

    @Override
    public Product getByName(String name) {
        return productRepository.findByName(name).orElseThrow(() ->
                new ProductNotFoundException("Product with name = " + name + " not found"));
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();

        if (products == null || products.isEmpty()) {
            throw new ProductNotFoundException("Products not found");
        }

        return products;
    }

    @Override
    public void add(Product product) {
        if (productRepository.existsById(product.getId())) {
            throw new ProductExistsException("Product with id = " + product.getId() + " exists");
        }
        if (productRepository.existsByName(product.getName())) {
            throw new ProductExistsException("Product with name = " + product.getName() + " exists");
        }

        productRepository.save(product);
    }

    @Override
    public void update(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new ProductNotFoundException("Product with id = " + product.getId() + " not found");
        }

        productRepository.save(product);
    }
}

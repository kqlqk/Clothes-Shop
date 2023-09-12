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
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product with id = " + id + " not found"));
    }

    @Override
    public Product getByName(String name) {
        return productRepository.findByName(name).orElseThrow(() -> new ProductNotFoundException("Product with name = " + name + " not found"));
    }

    @Override
    public List<Product> getAll() {
        List<Product> products = productRepository.findAll();

        if (products.isEmpty()) {
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
        if (productRepository.existsByPath(product.getPath())) {
            throw new ProductExistsException("Product with path = " + product.getPath() + " exists");
        }

        productRepository.save(product);
    }

    @Override
    public void update(Product product) {
        if (!productRepository.existsById(product.getId())) {
            throw new ProductNotFoundException("Product with id = " + product.getId() + " not found");
        }

        Product productDb = getById(product.getId());

        if (product.getName() == null || product.getName().isBlank()) {
            product.setName(productDb.getName());
        }

        if (product.getPrice() == 0) {
            product.setPrice(productDb.getPrice());
        }

        if (product.getDescription() == null || product.getDescription().isBlank()) {
            product.setDescription(productDb.getDescription());
        }

        if (product.getColors() == null || product.getColors().isEmpty()) {
            product.setColors(productDb.getColors());
        }

        if (product.getSizes() == null || product.getSizes().isEmpty()) {
            product.setSizes(productDb.getSizes());
        }

        if (product.getPath() == null || product.getPath().isBlank()) {
            product.setPath(productDb.getPath());
        }

        productRepository.save(product);
    }

    @Override
    public List<Product> getSales() {
        return productRepository.findSales();
    }

    @Override
    public List<Product> getLastProducts(int limit) {
        return productRepository.findLastProducts(limit);
    }
}

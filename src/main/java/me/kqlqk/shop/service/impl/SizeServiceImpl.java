package me.kqlqk.shop.service.impl;

import me.kqlqk.shop.exception.ColorExistsException;
import me.kqlqk.shop.exception.ColorNotFoundException;
import me.kqlqk.shop.model.Size;
import me.kqlqk.shop.model.enums.Sizes;
import me.kqlqk.shop.repository.SizeRepository;
import me.kqlqk.shop.service.SizeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SizeServiceImpl implements SizeService {
    private final SizeRepository sizeRepository;

    @Autowired
    public SizeServiceImpl(SizeRepository sizeRepository) {
        this.sizeRepository = sizeRepository;
    }


    @Override
    public Size getById(long id) {
        return sizeRepository.findById(id).orElseThrow(() ->
                new ColorNotFoundException("Size with id = " + id + " not found"));
    }

    @Override
    public Size getByName(Sizes name) {
        return sizeRepository.findByName(name).orElseThrow(() ->
                new ColorNotFoundException("Size with name = " + name + " not found"));
    }

    @Override
    public void add(Size size) {
        if (sizeRepository.existsById(size.getId())) {
            throw new ColorExistsException("Size with id = " + size.getId() + " exists");
        }
        if (sizeRepository.existsByName(size.getName())) {
            throw new ColorExistsException("Size with name = " + size.getName().name() + " exists");
        }

        sizeRepository.save(size);
    }

    @Override
    public void update(Size size) {
        if (!sizeRepository.existsById(size.getId())) {
            throw new ColorNotFoundException("Size with id = " + size.getId() + " not found");
        }

        sizeRepository.save(size);
    }
}

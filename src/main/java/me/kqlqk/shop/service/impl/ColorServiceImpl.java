package me.kqlqk.shop.service.impl;

import me.kqlqk.shop.exception.ColorExistsException;
import me.kqlqk.shop.exception.ColorNotFoundException;
import me.kqlqk.shop.model.Color;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.repository.ColorRepository;
import me.kqlqk.shop.service.ColorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColorServiceImpl implements ColorService {
    private final ColorRepository colorRepository;

    @Autowired
    public ColorServiceImpl(ColorRepository colorRepository) {
        this.colorRepository = colorRepository;
    }


    @Override
    public Color getById(long id) {
        return colorRepository.findById(id).orElseThrow(() ->
                new ColorNotFoundException("Color with id = " + id + " not found"));
    }

    @Override
    public Color getByName(Colors name) {
        return colorRepository.findByName(name).orElseThrow(() ->
                new ColorNotFoundException("Color with id = " + name + " not found"));
    }

    @Override
    public void add(Color color) {
        if (colorRepository.existsById(color.getId())) {
            throw new ColorExistsException("Color with id = " + color.getId() + " exists");
        }
        if (colorRepository.existsByName(color.getName())) {
            throw new ColorExistsException("Color with name = " + color.getName().name() + " exists");
        }

        colorRepository.save(color);
    }

    @Override
    public void update(Color color) {
        if (!colorRepository.existsById(color.getId())) {
            throw new ColorNotFoundException("Color with id = " + color.getId() + " not found");
        }

        colorRepository.save(color);
    }
}

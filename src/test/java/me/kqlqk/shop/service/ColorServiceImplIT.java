package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.ColorExistsException;
import me.kqlqk.shop.exception.ColorNotFoundException;
import me.kqlqk.shop.model.Color;
import me.kqlqk.shop.model.enums.Colors;
import me.kqlqk.shop.repository.ColorRepository;
import me.kqlqk.shop.service.impl.ColorServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class ColorServiceImplIT {
    @Autowired
    private ColorServiceImpl colorService;

    @Autowired
    private ColorRepository colorRepository;

    @Test
    public void add_shouldAddTagToDB() {
        int oldSize = colorRepository.findAll().size();

        Color color = new Color(Colors.GRAY);

        colorService.add(color);

        int newSize = colorRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        Color color = colorService.getById(1);
        color.setName(Colors.GRAY);
        Color finalColor1 = color;
        assertThrows(ColorExistsException.class, () -> colorService.add(finalColor1));

        color = colorService.getById(1);
        color.setId(99);
        Color finalColor2 = color;
        assertThrows(ColorExistsException.class, () -> colorService.add(finalColor2));
    }

    @Test
    public void update_shouldUpdateProductInDB() {
        Color color = colorService.getById(1);
        color.setName(Colors.GRAY);

        colorService.update(color);

        assertThat(colorService.getById(1).getName()).isEqualTo(color.getName());
    }

    @Test
    public void update_shouldThrowException() {
        Color color = new Color(Colors.GRAY);
        color.setId(99);
        assertThrows(ColorNotFoundException.class, () -> colorService.update(color));
    }
}

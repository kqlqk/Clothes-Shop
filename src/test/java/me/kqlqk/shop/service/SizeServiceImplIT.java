package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.ColorExistsException;
import me.kqlqk.shop.exception.ColorNotFoundException;
import me.kqlqk.shop.model.Size;
import me.kqlqk.shop.model.enums.Sizes;
import me.kqlqk.shop.repository.SizeRepository;
import me.kqlqk.shop.service.impl.SizeServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class SizeServiceImplIT {
    @Autowired
    private SizeServiceImpl sizeService;

    @Autowired
    private SizeRepository sizeRepository;

    @Test
    public void add_shouldAddTagToDB() {
        int oldSize = sizeRepository.findAll().size();

        Size size = new Size(Sizes.XS);

        sizeService.add(size);

        int newSize = sizeRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        Size size = sizeService.getById(1);
        size.setName(Sizes.XS);
        Size finalSize1 = size;
        assertThrows(ColorExistsException.class, () -> sizeService.add(finalSize1));

        size = sizeService.getById(1);
        size.setId(99);
        Size finalSize2 = size;
        assertThrows(ColorExistsException.class, () -> sizeService.add(finalSize2));
    }

    @Test
    public void update_shouldUpdateProductInDB() {
        Size size = sizeService.getById(1);
        size.setName(Sizes.XS);

        sizeService.update(size);

        assertThat(sizeService.getById(1).getName()).isEqualTo(size.getName());
    }

    @Test
    public void update_shouldThrowException() {
        Size size = new Size(Sizes.XS);
        size.setId(99);
        assertThrows(ColorNotFoundException.class, () -> sizeService.update(size));
    }
}

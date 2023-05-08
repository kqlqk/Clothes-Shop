package me.kqlqk.shop.service;

import me.kqlqk.shop.ServiceTest;
import me.kqlqk.shop.exception.TagExistsException;
import me.kqlqk.shop.exception.TagNotFoundException;
import me.kqlqk.shop.model.Tag;
import me.kqlqk.shop.model.Tags;
import me.kqlqk.shop.repository.TagRepository;
import me.kqlqk.shop.service.impl.TagServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ServiceTest
public class TagServiceImplIT {
    @Autowired
    private TagServiceImpl tagService;

    @Autowired
    private TagRepository tagRepository;

    @Test
    public void add_shouldAddTagToDB() {
        int oldSize = tagRepository.findAll().size();

        Tag tag = new Tag(Tags.XS);

        tagService.add(tag);

        int newSize = tagRepository.findAll().size();

        assertThat(oldSize + 1).isEqualTo(newSize);
    }

    @Test
    public void add_shouldThrowException() {
        Tag tag = tagService.getById(1);
        tag.setName(Tags.WHITE);
        Tag finalTag1 = tag;
        assertThrows(TagExistsException.class, () -> tagService.add(finalTag1));

        tag = tagService.getById(1);
        tag.setId(99);
        Tag finalTag2 = tag;
        assertThrows(TagExistsException.class, () -> tagService.add(finalTag2));
    }

    @Test
    public void update_shouldUpdateProductInDB() {
        Tag tag = tagService.getById(1);
        tag.setName(Tags.GRAY);

        tagService.update(tag);

        assertThat(tagService.getById(1).getName()).isEqualTo(tag.getName());
    }

    @Test
    public void update_shouldThrowException() {
        Tag tag = new Tag(Tags.M);
        tag.setId(99);
        assertThrows(TagNotFoundException.class, () -> tagService.update(tag));
    }
}

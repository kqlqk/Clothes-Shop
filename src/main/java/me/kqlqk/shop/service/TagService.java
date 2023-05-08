package me.kqlqk.shop.service;

import me.kqlqk.shop.model.Tag;
import me.kqlqk.shop.model.Tags;
import org.springframework.stereotype.Service;

@Service
public interface TagService {
    Tag getById(long id);

    Tag getByName(Tags name);

    void add(Tag tag);

    void update(Tag tag);
}

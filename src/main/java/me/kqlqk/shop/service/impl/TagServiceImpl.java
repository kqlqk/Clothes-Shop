package me.kqlqk.shop.service.impl;

import me.kqlqk.shop.exception.TagExistsException;
import me.kqlqk.shop.exception.TagNotFoundException;
import me.kqlqk.shop.model.Tag;
import me.kqlqk.shop.model.Tags;
import me.kqlqk.shop.repository.TagRepository;
import me.kqlqk.shop.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    @Override
    public Tag getById(long id) {
        return tagRepository.findById(id).orElseThrow(() ->
                new TagNotFoundException("Tag with id = " + id + " not found"));
    }

    @Override
    public Tag getByName(Tags name) {
        return tagRepository.findByName(name).orElseThrow(() ->
                new TagNotFoundException("Tag with id = " + name + " not found"));
    }

    @Override
    public void add(Tag tag) {
        if (tagRepository.existsById(tag.getId())) {
            throw new TagExistsException("Tag with id = " + tag.getId() + " exists");
        }
        if (tagRepository.existsByName(tag.getName())) {
            throw new TagExistsException("Tag with name = " + tag.getName().name() + " exists");
        }

        tagRepository.save(tag);
    }

    @Override
    public void update(Tag tag) {
        if (!tagRepository.existsById(tag.getId())) {
            throw new TagNotFoundException("Tag with id = " + tag.getId() + " not found");
        }

        tagRepository.save(tag);
    }
}

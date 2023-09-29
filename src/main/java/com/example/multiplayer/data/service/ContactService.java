package com.example.multiplayer.data.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.example.multiplayer.data.entity.Contact;

@Service
public class ContactService {

    private final ContactRepository repository;

    public ContactService(ContactRepository repository) {
        this.repository = repository;
    }

    public Optional<Contact> get(Long id) {
        return repository.findById(id);
    }

    public Contact update(Contact entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public Page<Contact> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public Page<Contact> list(Pageable pageable,
            Specification<Contact> filter) {
        return repository.findAll(filter, pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}

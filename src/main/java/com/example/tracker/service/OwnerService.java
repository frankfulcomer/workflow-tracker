package com.example.tracker.service;

import com.example.tracker.model.Owner;
import com.example.tracker.repository.OwnerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerService {

    private final OwnerRepository repository;

    public OwnerService(OwnerRepository repository) {
        this.repository = repository;
    }

    public List<Owner> findAll() {
        return repository.findAll();
    }
}

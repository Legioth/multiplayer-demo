package com.example.multiplayer.data.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.example.multiplayer.data.entity.SamplePerson;

public interface SamplePersonRepository
        extends JpaRepository<SamplePerson, Long>,
        JpaSpecificationExecutor<SamplePerson> {

}

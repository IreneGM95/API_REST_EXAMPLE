package com.example.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.example.entities.Presentacion;

public interface PresentacionService {
    
    public List<Presentacion> findAll(Sort sort);
    public Page<Presentacion> findAll(Pageable pageable);
    /** Encontrar un presentacion concreto por su id: */
    public Presentacion findById (long id);

    public Presentacion save (Presentacion presentacion);
    public void delete(Presentacion presentacion);
}
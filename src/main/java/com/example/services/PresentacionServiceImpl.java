package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.dao.PresentacionDao;
import com.example.entities.Presentacion;

@Service
public class PresentacionServiceImpl implements PresentacionService {

    /** Creamos la variable de tipo Dao para poder inyectarle la capa DAO, puede resolverse con un @Autowire o mediante constructor: */
    @Autowired
    private PresentacionDao presentacionDao;

    @Override
    public List<Presentacion> findAll(Sort sort) {
        return presentacionDao.findAll(sort);

    }

    @Override
    public Page<Presentacion> findAll(Pageable pageable) {
        
        return presentacionDao.findAll(pageable);
    }

    @Override
    public Presentacion findById(long id) {
        return presentacionDao.findById(id);
    }

    @Override
    public Presentacion save(Presentacion presentacion) {
       return presentacionDao.save(presentacion);
    }

    @Override
    public void delete(Presentacion presentacion) {
        presentacionDao.delete(presentacion);
    }

}

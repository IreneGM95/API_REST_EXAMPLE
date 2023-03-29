package com.example.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//En lugar de controller usamos un restcontroller
//En una api rest se gestiona un recurso y en dependencias de http sera la peticion u otra
@RestController
@RequestMapping("/productos") // todas las peticiones se hacen a traves de productos, no hace falta crear
                              // /listar, /alta...
public class ProductoController {

    /**
     * El método siguiente es de ejemplo para entender el formato JSON y no tiene
     * que ver en sí con el proyecto para examen
     */
    @GetMapping
    public List<String> nombre() {
        List<String> nombres = Arrays.asList("salma", "Elisabet", "Judith");
        return nombres;
    }

}

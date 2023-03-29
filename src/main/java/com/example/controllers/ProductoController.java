package com.example.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

//En lugar de controller usamos un restcontroller
//En una api rest se gestiona un recurso y en dependencias de http sera la peticion u otra
@RestController
@RequestMapping("/productos") // todas las peticiones se hacen a traves de productos, no hace falta crear
                              // /listar, /alta...
public class ProductoController {

    // No basta devolver la información, hay que confirmar la petición
    @Autowired
    private ProductoService productoService;

    /**
     * Este método va a responder a una petición (request) del tipo
     * http://localhost:8080/productos?page=1&size=4, pagina uno con 4 productos
     * Debe ser capaz de devolver un listado un listado de productos paginados o no,
     * pero en cualquier caso ordenado por un criterio (nombre, descripción, etc.)
     * La petición anterior iplica un @RequestParam
     * "page" es el número de pagina
     * 
     * @PathVariable sirve para peticiones del tipo /productos/3
     */
    @GetMapping // le hacemos la petición por get
    public ResponseEntity<List<Producto>> findAll(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size) {

        ResponseEntity<List<Producto>> responseEntity = null;
        List<Producto> productos = new ArrayList<>();

        Sort sortByNombre = Sort.by("nombre");

        // comprobamos si tenemos paginas y productos:
        if (page != null && size != null) {
            // con paginación y ordenación:
            try {
                Pageable pageable = PageRequest.of(page, size, sortByNombre);
                Page<Producto> productosPaginados = productoService.findAll(pageable);
                productos = productosPaginados.getContent();
                responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);
            } catch (Exception e) {
                responseEntity = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        } else {
            // sin paginación, pero con ordenamiento:
            try {
                productos = productoService.findAll(sortByNombre);
                responseEntity = new ResponseEntity<List<Producto>>(productos, HttpStatus.OK);
            } catch (Exception e) {
                responseEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }

        return responseEntity;

    }

    // /**
    // * El método siguiente es de ejemplo para entender el formato JSON y no tiene
    // * que ver en sí con el proyecto para examen
    // */
    // @GetMapping
    // public List<String> nombre() {
    // List<String> nombres = Arrays.asList("salma", "Elisabet", "Judith");
    // return nombres;
    // }

}

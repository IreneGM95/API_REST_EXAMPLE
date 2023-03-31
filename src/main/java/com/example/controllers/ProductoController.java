package com.example.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.entities.Producto;
import com.example.services.ProductoService;

import jakarta.validation.Valid;

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

    /**
     * Recuperar un producto por el id:
     * Responde a una petición del tipo http://localhost:8080/productos/2 donde id=2
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> findById(@PathVariable(name = "id") Integer id) {

        ResponseEntity<Map<String, Object>> responseEntity = null;
        Map<String, Object> responseAsMap = new HashMap<>();

        try {
            Producto producto = productoService.findById(id);
            if (producto != null) {
                String successMessage = "Se ha encontrado el producto con id: " + id;
                responseAsMap.put("mensaje", successMessage);
                responseAsMap.put("producto", producto);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
            } else {
                String errorMessage = "No se ha podido encontrar el producto con id: " + id;
                responseAsMap.put("erros", errorMessage);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.NOT_FOUND);

            }

        } catch (Exception e) {
            String errorGrave = "Error grave";
            responseAsMap.put("error", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;
    }

    /**
     * Persiste un producto en la base de datos y este método podríamos llamarlo
     * insert o save porque inserta/guarda un objeto producto en la base de datos
     * Se anota con post porque recibe los datos atraves de un formulario o cuerpo
     * de la petición.
     * En este caso, al ser un post, produto viene dentro de la petición, mientras
     * que en el post va en la cabecera (la url), por eso se utiliza un @RequestBody
     * 
     * @Valid obliga a que se cumplan los requisitos e informa de cuándo no se están
     *        cumpliendo
     *        Y para poder manejarlo es necesario un BindingResult
     * 
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> insert(@Valid @RequestBody Producto producto,
            BindingResult result) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;
        /** Primero hay que comprobar si hay errores en el producto recibido: */
        if (result.hasErrors()) {
            List<String> errorMessages = new ArrayList<>();

            for (ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage()); // el default message es que pusimos como anotación en la
                                                              // entidad Producto @NotNull
            }
            responseAsMap.put("errores", errorMessages);

            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
            return responseEntity;

        }
        // Si no hay errores, persistimos el producto. No se usa un else porque no es
        // vinculante
        Producto productoDB = productoService.save((producto));

        try {
            if (productoDB != null) {
                String mensaje = "El producto se ha creado correctamente";
                responseAsMap.put("mensaje", mensaje);
                responseAsMap.put("producto", productoDB);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.CREATED);
            } else {

            }
        } catch (DataAccessException e) {
            String errorGrave = "Se ha producido un error grave, y la cauda más probable puede ser: "
                    + e.getMostSpecificCause();
            responseAsMap.put("errorGrave", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);

        }

        return responseEntity;
    }

    /** Método para actualizar */
    /**
     * El metodo siguiente actualiza un producto en la base de datos
     */

    @PostMapping("/{id}") // le pasamos el id del prodcuto
    @Transactional

    public ResponseEntity<Map<String, Object>> update(@Valid @RequestBody Producto producto,
            BindingResult result,
            @PathVariable(name = "id") Integer id) {

        Map<String, Object> responseAsMap = new HashMap<>();
        ResponseEntity<Map<String, Object>> responseEntity = null;

        // Primero comprobar si hay errores en el producto recibido

        if (result.hasErrors()) {
            List<String> errorMessages = new ArrayList<>();

            for (ObjectError error : result.getAllErrors()) {
                errorMessages.add(error.getDefaultMessage());
            }

            responseAsMap.put("errores", errorMessages);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.BAD_REQUEST);
            return responseEntity;

        }
        // SI NO HAY ERRORES, ENTONCES PERSISTIMOS EL PRODUCTO
        // Vinculando previamente el id que se recibe con el producto
        producto.setId(id);
        Producto productoDB = productoService.save(producto);

        try {
            if (productoDB != null) {
                String mensaje = "El producto se ha actualizado correctamente";
                responseAsMap.put("mensaje", mensaje);
                responseAsMap.put("producto", productoDB);
                responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.OK);
            }

            else {
                // En caso que no se haya creado el prodcuto
            }
        } catch (DataAccessException e) {

            String errorGrave = "Ha tenido lugar un error grave, y la causa mas probable puede ser"
                    + e.getMostSpecificCause();
            responseAsMap.put("errorGrave", errorGrave);
            responseEntity = new ResponseEntity<Map<String, Object>>(responseAsMap, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return responseEntity;

    }

    /**
     * Método para borrar, no puede ser void porque necesitamos que nos devuelva un
     * mensaje
     */
    @DeleteMapping("/{id}") // le pasamos el id del prodcuto
    @Transactional
    public ResponseEntity<String> deleteProducto(@PathVariable(name = "id") Integer id) {

        ResponseEntity<String> responseEntity = null;

        try {

            // Primero lo recuperamos
            Producto producto = productoService.findById((id));
            // Si existe, lo borramos
            if (producto != null) {
                productoService.delete(producto);

                responseEntity = new ResponseEntity<String>("El producto se ha borrado correctamente", HttpStatus.OK);
            } else {
                // De lo contrario, informamos de que no existe
                responseEntity = new ResponseEntity<String>("Este producto no existe", HttpStatus.NOT_FOUND);

            }

        } catch (DataAccessException e) {
            e.getMostSpecificCause();
            responseEntity = new ResponseEntity<String>("Error fatal", HttpStatus.INTERNAL_SERVER_ERROR);
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

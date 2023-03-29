package com.example.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.entities.Producto;

public interface ProductoDao extends JpaRepository<Producto, Long> {

    /**
     * Vamos a necesitar tres metodos adicionalesa los que genera el CRUD repository
     * (la interface) para:
     * 1. Recuperar la lista de productos ordenados
     * 2. Recuperar listado de productos paginados, es decir, que no traiga todos
     * los productos de golpe sino de 10 en 10, 20 en 20...
     * 3. Consulta para recuperar las presentaciones con sus productos
     * correspondientes sin tener que realizar una subconsultala cual sería menos
     * eficiente que un join a las entidades utilizando HQL (Hibernate Query
     * Language)
     */

    // Recuperar la lista de productos ordenados:

    @Query(value = "select p from Producto p left join fetch p.presentacion") // NO es una consulta de mysql, sino HQL
    public List<Producto> findAll(Sort sort);

    // Recuperar listado de productos paginados:

    @Query(value = "select p from Producto p left join fetch p.presentacion", countQuery = "select count(p) from Producto p left join p.presentacion")
    public Page<Producto> findAll(Pageable pageable);
//devuelve el numero de productos fijados por el pageable


    // Consulta para recuperar las presentaciones con sus productos utilizando HQL
    // (Hibernate Quer Language), se recupera por el id:

    @Query(value = "select p from Producto p left join fetch p.presentacion where p.id = :id")
    public Producto findById(long id);

    /**
     * RECORDEMOS QUE: Cuando hemos creado las relaciones hemos especificado que
     * la busqueda sea LAZY, para que no se traiga la presentacion siempre que se
     * busque un producto, porque serian dos consultas, o una consulta con una
     * subconsulta, que es menos eficiente que lo que vamos a hacer, hacer una sola
     * consulta relacionando las entidades, y digo las entidades, porque aunque
     * de la impresión que es una consulta de SQL no consultamos a las tablas de
     * la base de datos sino a las entidades
     * (esto se llama HQL (Hibernate Query Language))
     * 
     * Ademas, tambien podremos recuperar el listado de productos de forma ordenada,
     * por algun criterio de ordenación, como por
     * ejemplo por el nombre del producto,
     * por la descripcion, etc.
     */
}

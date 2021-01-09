package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShipService {

    Ship getById(Long id);

    void create(Ship ship);

    void delete(Long id);

    List<Ship> getAll();

    long getCount();

    Ship update(Long id, Ship ship);

    Ship getShip(Long id);

    /**++++++++++++++  My experiments  +++++++++++++++++*/

    Long checkId(String id);

    Page<Ship> findAllShips(Pageable pageable);

}


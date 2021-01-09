package com.space.service;

import com.space.model.Ship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShipService {

    Ship getById(Long id);

    void creat(Ship ship);

    void delete(Long id);

    List<Ship> getAll();

    long getCount();

    void update(Long id);



    /**++++++++++++++  My experiments  +++++++++++++++++*/



    List<Ship> getAllShips();

    void saveShip(Ship ship);

    Ship getShipById(long id);

    void deleteShipById(long id);

    Page<Ship> findAllShips(Pageable pageable);

}


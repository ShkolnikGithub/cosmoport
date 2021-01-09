package com.space.service;

import com.space.model.Ship;
import com.space.model.ShipType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface ShipService {

    Page<Ship> getShipsList(Specification<Ship> specification, Pageable sortedBy);

    Integer getShipsCount(Specification<Ship> specification);

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


    /**++++++++++++++  Specification methods  +++++++++++++++++*/


    Specification<Ship> selectByName(String name);
    Specification<Ship> selectByPlanet(String planet);
    Specification<Ship> selectByShipType(ShipType shipType);
    Specification<Ship> selectByProdDate(Long after, Long before);
    Specification<Ship> selectByUse(Boolean isUsed);
    Specification<Ship> selectBySpeed(Double minSpeed, Double maxSpeed);
    Specification<Ship> selectByCrewSize(Integer minCrewSize, Integer maxCrewSize);
    Specification<Ship> selectByRating(Double minRating, Double maxRating);

}


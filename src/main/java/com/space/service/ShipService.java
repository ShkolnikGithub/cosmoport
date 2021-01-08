package com.space.service;

import com.space.model.Ship;

import java.util.List;

public interface ShipService {

    Ship getById(Long id);

    void creat(Ship ship);

    void delete(Long id);

    List<Ship> getAll();

    long getCount();

    void update(Long id);
}


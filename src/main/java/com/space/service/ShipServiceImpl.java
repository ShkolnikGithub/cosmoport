package com.space.service;

import com.space.model.Ship;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {

    final ShipRepository shipRepository;

    @Autowired
    public ShipServiceImpl(ShipRepository shipRepository) {
        this.shipRepository = shipRepository;
    }

    @Override
    public Ship getById(Long id) {
        return shipRepository.findById(id).orElse(null);
    }

    @Override
    public void creat(Ship ship) {
        shipRepository.save(ship);
    }

    @Override
    public void delete(Long id) {
        shipRepository.deleteById(id);
    }

    @Override
    public List<Ship> getAll() {
        return shipRepository.findAll();
    }

    @Override
    public long getCount() {
        return shipRepository.count();
    }

    @Override
    public void update(Long id) {
        shipRepository.flush();
    }
}


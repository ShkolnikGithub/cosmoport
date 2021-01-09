package com.space.service;

import com.space.exceptions.BadRequestException;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    public void create(Ship ship) {
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
    public Ship update(Long id, Ship ship) {
        Ship newShip = getShip(id);

        String name = ship.getName();
        if (name != null) {
            checkShipName(ship);
            newShip.setName(name);
        }

        String planet = ship.getPlanet();
        if (planet != null) {
            checkShipPlanet(ship);
            newShip.setPlanet(planet);
        }

        ShipType shipType = ship.getShipType();
        if (shipType != null) {
            newShip.setShipType(shipType);
        }

        Date prodDate = ship.getProdDate();
        if (prodDate != null) {
            checkShipProdDate(ship);
            newShip.setProdDate(prodDate);
        }

        Boolean isUsed = ship.getUsed();
        if (isUsed != null) {
            newShip.setUsed(isUsed);
        }

        Double speed = ship.getSpeed();
        if (speed != null) {
            checkShipSpeed(ship);
            newShip.setSpeed(speed);
        }

        Integer crewSize = ship.getCrewSize();
        if (crewSize != null) {
            checkShipCrewSize(ship);
            newShip.setCrewSize(crewSize);
        }

        Double rating = remakeRating(newShip);
        newShip.setRating(rating);

        return shipRepository.save(newShip);
    }

    @Override
    public Long checkId(String id) {
        Long longId = null;

        if (id == null || id.equals("") || id.equals("0")) {
            throw new BadRequestException();
        }

        try {
            longId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new BadRequestException();
        }

        if (!shipRepository.existsById(longId)) {
            throw new NotFoundException();
        }

        return longId;
    }

    @Override
    public Ship getShip(Long id) {
        if (!shipRepository.existsById(id)) {
            throw new NotFoundException();
        }

        return shipRepository.findById(id).get();
    }

    @Override
    public Page<Ship> findAllShips(Pageable pageable) {
        return this.shipRepository.findAll(pageable);
    }

    private void checkShipName(Ship ship) {
        String name = ship.getName();
        if (name.length() < 1 || name.length() > 50) {
            throw new BadRequestException();
        }
    }

    private void checkShipPlanet(Ship ship) {
        String planet = ship.getPlanet();
        if (planet.length() < 1 || planet.length() > 50) {
            throw new BadRequestException();
        }
    }

    private void checkShipProdDate(Ship ship) {
        Date prodDate = ship.getProdDate();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(prodDate);
        int year = calendar.get(Calendar.YEAR);
        if (year < 2800 || year > 3019) {
            throw new BadRequestException();
        }
    }

    private void checkShipSpeed(Ship ship) {
        Double speed = ship.getSpeed();
        if (speed < 0.01 || speed > 0.99) {
            throw new BadRequestException();
        }
    }

    private void checkShipCrewSize(Ship ship) {
        Integer crewSize = ship.getCrewSize();
        if (crewSize < 1 || crewSize > 9999) {
            throw new BadRequestException();
        }
    }

    private Double remakeRating(Ship ship) {
        double k = ship.getUsed() ? 0.5 : 1;
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(ship.getProdDate());
        int prodYear = calendar.get(Calendar.YEAR);
        BigDecimal rating = BigDecimal.valueOf((80 * ship.getSpeed() * k) /
                (3019 - prodYear + 1)).setScale(2, RoundingMode.HALF_UP);
        return rating.doubleValue();
    }
}


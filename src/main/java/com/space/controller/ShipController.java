package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {
    private final ShipService shipService;
    public static long counter;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public ResponseEntity<List<Ship>> getAllShips(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "planet", required = false) String planet,
            @RequestParam(value = "shipType", required = false) ShipType shipType,
            @RequestParam(value = "after", required = false) Long after,
            @RequestParam(value = "before", required = false) Long before,
            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "maxRating", required = false) Double maxRating,
            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));

        Specification<Ship> specification = Specification.where(shipService.selectByName(name)
                .and(shipService.selectByPlanet(planet))
                .and(shipService.selectByProdDate(after, before))
                .and(shipService.selectByUse(isUsed))
                .and(shipService.selectBySpeed(minSpeed, maxSpeed))
                .and(shipService.selectByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.selectByShipType(shipType))
                .and(shipService.selectByRating(minRating, maxRating)));

        return new ResponseEntity<>(shipService.getShipsList(specification, pageable).getContent(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ship> getShip(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship ship = this.shipService.getById(id);
        if (ship == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Integer> getCount(@RequestParam(value = "name", required = false) String name,
                                            @RequestParam(value = "planet", required = false) String planet,
                                            @RequestParam(value = "shipType", required = false) ShipType shipType,
                                            @RequestParam(value = "after", required = false) Long after,
                                            @RequestParam(value = "before", required = false) Long before,
                                            @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                            @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                            @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                            @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                            @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                            @RequestParam(value = "minRating", required = false) Double minRating,
                                            @RequestParam(value = "maxRating", required = false) Double maxRating) {

        Specification<Ship> specification = Specification.where(shipService.selectByName(name)
                .and(shipService.selectByPlanet(planet))
                .and(shipService.selectByShipType(shipType))
                .and(shipService.selectByProdDate(after, before))
                .and(shipService.selectByUse(isUsed))
                .and(shipService.selectBySpeed(minSpeed, maxSpeed))
                .and(shipService.selectByCrewSize(minCrewSize, maxCrewSize))
                .and(shipService.selectByRating(minRating, maxRating)));

        return new ResponseEntity<>(shipService.getShipsCount(specification), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Ship> createShip(@RequestParam(defaultValue = "false") Boolean isUsed,
                                           @RequestBody Ship ship) {
        if (ship.getName() == null
                || ship.getPlanet() == null
                || ship.getShipType() == null
                || ship.getProdDate() == null
                || ship.getSpeed() == null
                || ship.getCrewSize() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (ship.getUsed() == null) {
            ship.setUsed(isUsed);
        }

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);

        if (ship.getName().length() == 0
                || ship.getName().length() > 50
                || ship.getPlanet().length() == 0
                || ship.getPlanet().length() > 50
                || ship.getSpeed() < 0.01
                || ship.getSpeed() > 0.99
                || ship.getCrewSize() < 1
                || ship.getCrewSize() > 9999
                || year < 2800
                || year > 3019) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ship.setRating(getRating(ship));
        this.shipService.create(ship);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Ship> updateShip(@PathVariable String id,
                                           @RequestBody Ship ship) {
        Long longId = shipService.checkId(id);
        Ship newShip = shipService.update(longId, ship);

        return new ResponseEntity<>(newShip, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShipById(@PathVariable String id) {
        Long longId = shipService.checkId(id);
        shipService.delete(longId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    public double isUsed(Boolean isUsed) {
        return isUsed ? 0.5 : 1.0;
    }

    public double getRating(Ship ship) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(ship.getProdDate());
        int year = calendar.get(Calendar.YEAR);
        BigDecimal rating = new BigDecimal((80 * ship.getSpeed() * (ship.getUsed() ? 0.5 : 1)) /
                (3019 - year + 1));
        rating = rating.setScale(2, RoundingMode.HALF_UP);
        return rating.doubleValue();
    }
}


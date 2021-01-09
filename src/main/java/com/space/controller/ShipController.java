package com.space.controller;

import com.space.exceptions.BadRequestException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/rest/ships")
public class ShipController {
    private ShipService shipService;
    public static long counter;
    private List<Ship> ships;

    @Autowired
    public ShipController(ShipService shipService) {
        this.shipService = shipService;
    }

    @GetMapping
    public List<Ship> getAllShips(
            @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Page<Ship> pageAll = shipService.findAllShips(pageable);

        List<Ship> listShips = pageAll.getContent();

        return listShips;
    }

//    @GetMapping
//    public List<Ship> getAllShips() {
//        ships = shipService.getAll();
//        return ships;
//    }

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
    public int getCount() {
        return (int) shipService.getCount();
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

        if (ship.getName().length() == 0
                || ship.getName().length() > 50
                || ship.getPlanet().length() == 0
                || ship.getPlanet().length() > 50
                || ship.getSpeed() < 0.01
                || ship.getSpeed() > 0.99
                || ship.getCrewSize() < 1
                || ship.getCrewSize() > 9999
                || (ship.getProdDate().getYear() + 1900) < 2800
                || (ship.getProdDate().getYear() + 1900) > 3019) {
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


    /**++++++++++++++  My experiments
     *
     *
     *
     *
     *
     *
     *
     *
     *
     * +++++++++++++++++*/


}


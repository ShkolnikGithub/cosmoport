package com.space.controller;

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
            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
            Model model) {

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        Page<Ship> pageAll = shipService.findAllShips(pageable);

        //Page<Ship> page = shipService.findAllShips(pageNumber, pageSize);
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
    public ResponseEntity<Ship> create(@RequestParam(defaultValue = "false") Boolean isUsed,
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

        Double rating = (80 * ship.getSpeed() * isUsed(ship.getUsed())) /
                (3019 - (ship.getProdDate().getYear() + 1900) + 1);
        ship.setRating(rating);
        this.shipService.creat(ship);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @PostMapping("/{id}")
    public ResponseEntity<Ship> update(@PathVariable(value = "id", required = false) Long id,
                                       @RequestParam(value = "name", required = false) String name,
                                       @RequestParam(value = "planet", required = false) String planet,
                                       @RequestParam(value = "shipType", required = false) ShipType shipType,
                                       @RequestParam(value = "prodDate", required = false) Date prodDate,
                                       @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                       @RequestParam(value = "speed", required = false) Double speed,
                                       @RequestParam(value = "crewSize", required = false) Integer crewSize,
                                       @RequestParam(value = "rating", required = false) Double rating,
                                       @RequestBody(required = false) Ship newShip) {

        if (id == null || newShip == null) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        if (id < 1L) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Ship ship = shipService.getById(id);
        if (ship == null ) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (name == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }


        if (name != null) {
            ship.setName(name);
        }

        if (planet != null) {
            ship.setPlanet(planet);
        }

        if (shipType != null) {
            ship.setShipType(shipType);
        }

        if (prodDate != null) {
            ship.setProdDate(prodDate);
        }

        if (isUsed != null) {
            ship.setUsed(isUsed);
        }

        if (speed != null) {
            ship.setSpeed(speed);
        }

        if (crewSize != null) {
            ship.setCrewSize(crewSize);
        }

        Double newRating = (80 * speed * isUsed(isUsed)) /
                (3019 - (prodDate.getYear() + 1900) + 1);
        ship.setRating(newRating);

        shipService.update(id);

        return new ResponseEntity<>(ship, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Ship> delete(@PathVariable("id") Long id) {
        if (id == null || id <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ships = shipService.getAll();

        for (long i = 0; i < ships.size(); i++) {
            if (ships.get((int) i).getId() == id) {
                shipService.delete(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public double isUsed(Boolean isUsed) {
        return isUsed ? 0.5 : 1.0;
    }


    /**++++++++++++++  My experiments  +++++++++++++++++*/


//    @GetMapping("/page/{pageNumber}")
//    public List<Ship> findPaginated(
////            @PathVariable(value = "pageNo") int pageNo, Model model
//            @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
//            @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize,
//            Model model
//
//    ) {
////        int pageSize = 5;
////        pageNo = 6;
//
//        Page<Ship> page = shipService.findPaginated(pageNumber, pageSize);
//        List<Ship> listShips = page.getContent();
//
////        model.addAttribute("currentPage", pageNumber);
////        model.addAttribute("totalPages", page.getTotalPages());
////        model.addAttribute("totalItems", page.getTotalElements());
////        model.addAttribute("listShips", listShips);
//
//        return listShips;
//    }


}


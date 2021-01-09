package com.space.service;

import com.space.exceptions.BadRequestException;
import com.space.exceptions.NotFoundException;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class ShipServiceImpl implements ShipService {

    final ShipRepository shipRepository;

    @Override
    public Page<Ship> getShipsList(Specification<Ship> specification, Pageable sortedBy) {
        return shipRepository.findAll(specification, sortedBy);
    }

    @Override
    public Integer getShipsCount(Specification<Ship> specification) {
        return shipRepository.findAll(specification).size();
    }

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

    /**
     * -------------  Select methods  -------------------------
     */

    @Override
    public Specification<Ship> selectByName(String name) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (name == null) {
                    return null;
                }
                return criteriaBuilder.like(root.get("name"), "%" + name + "%");
            }
        };
    }

    @Override
    public Specification<Ship> selectByPlanet(String planet) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (planet == null) {
                    return null;
                }
                return criteriaBuilder.like(root.get("planet"), "%" + planet + "%");
            }
        };
    }

    @Override
    public Specification<Ship> selectByShipType(ShipType shipType) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (shipType == null) {
                    return null;
                }
                return criteriaBuilder.equal(root.get("shipType"), shipType);
            }
        };
    }

    @Override
    public Specification<Ship> selectByProdDate(Long after, Long before) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (after == null && before == null) {
                    return null;
                }

                if (after == null) {
                    Date tempBefore = new Date(before);
                    return criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), tempBefore);
                }

                if (before == null) {
                    Date tempAfter = new Date(after);
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), tempAfter);
                }

                Calendar beforeCalendar = new GregorianCalendar();
                beforeCalendar.setTime(new Date(before));
                beforeCalendar.set(Calendar.HOUR, 0);
                beforeCalendar.add(Calendar.MILLISECOND, 0);

                Date tempAfter = new Date(after);
                Date tempBefore = beforeCalendar.getTime();

                return criteriaBuilder.between(root.get("prodDate"), tempAfter, tempBefore);
            }
        };
    }

    @Override
    public Specification<Ship> selectByUse(Boolean isUsed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (isUsed == null) {
                    return null;
                }
                if (isUsed) {
                    return criteriaBuilder.isTrue(root.get("isUsed"));
                } else {
                    return criteriaBuilder.isFalse(root.get("isUsed"));
                }
            }
        };
    }

    @Override
    public Specification<Ship> selectBySpeed(Double minSpeed, Double maxSpeed) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minSpeed == null && maxSpeed == null) {
                    return null;
                }
                if (minSpeed == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed);
                }
                if (maxSpeed == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed);
                }
                return criteriaBuilder.between(root.get("speed"), minSpeed, maxSpeed);
            }
        };
    }

    @Override
    public Specification<Ship> selectByCrewSize(Integer minCrewSize, Integer maxCrewSize) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minCrewSize == null && maxCrewSize == null) {
                    return null;
                }
                if (minCrewSize == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize);
                }
                if (maxCrewSize == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize);
                }
                return criteriaBuilder.between(root.get("crewSize"), minCrewSize, maxCrewSize);
            }
        };
    }

    @Override
    public Specification<Ship> selectByRating(Double minRating, Double maxRating) {
        return new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                if (minRating == null && maxRating == null) {
                    return null;
                }
                if (minRating == null) {
                    return criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating);
                }
                if (maxRating == null) {
                    return criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating);
                }
                return criteriaBuilder.between(root.get("rating"), minRating, maxRating);
            }
        };
    }

}


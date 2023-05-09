package davor.springframework.spring6restmvc.services;

import davor.springframework.spring6restmvc.model.Beer;

import java.util.UUID;

public interface BeerService {

    Beer getBeerById(UUID id);
}

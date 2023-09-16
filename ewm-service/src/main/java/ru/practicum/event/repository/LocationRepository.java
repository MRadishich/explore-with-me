package ru.practicum.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.event.entity.Location;

public interface LocationRepository extends JpaRepository<Location, Integer> {
    Location findByLatAndLon(double lat, double lon);
}

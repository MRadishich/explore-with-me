package ru.practicum.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.request.model.Request;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    boolean existsByEventIdAndRequesterId(Long eventId, Long userId);

    @Query("SELECT count(r.id) FROM Request AS r WHERE r.event.id in :eventId AND r.status = 'CONFIRMED' ")
    int countConfirmedRequests(Long eventId);

    List<Request> findByEventId(Long eventId);

    List<Request> findByRequesterId(Long userId);

    List<Request> findByEventIdAndRequesterId(Long eventId, Long userId);
}

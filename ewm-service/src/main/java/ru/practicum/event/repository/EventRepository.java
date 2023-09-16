package ru.practicum.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.event.entity.Event;
import ru.practicum.event.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query("SELECT e FROM Event e " +
            "WHERE (:users is null or e.initiator.id in (:users)) " +
            "AND (:states is null or e.state in (:states)) " +
            "AND (:categories is null or e.category.id in (:categories)) " +
            "AND (coalesce(:rangeStart, null) is null or e.eventDate >= :rangeStart)  " +
            "AND (coalesce(:rangeEnd, null) is null or e.eventDate <= :rangeEnd) " +
            "ORDER BY e.createdOn DESC")
    Page<Event> findAllByParamForAdmin(
            List<Long> users,
            List<EventState> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable pageable);

    @Query("SELECT E FROM Event AS E " +
            "LEFT JOIN E.requests AS PR " +
            "WHERE (:text is null or lower(E.annotation) like lower(CONCAT('%', :text, '%')) " +
            "or lower(E.description) like lower(CONCAT('%', :text, '%'))) " +
            "AND (:categories is null or E.category.id in (:categories)) " +
            "AND (:paid is null or E.paid in :paid) " +
            "AND (coalesce(:rangeStart, null) is null or E.eventDate >= :rangeStart) " +
            "AND (coalesce(:rangeEnd, null) is null or E.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = FALSE or (E.participantLimit = 0 or E.requestModeration = FALSE " +
            "or E.participantLimit > (SELECT count (PR.id) FROM PR WHERE PR.status = 'CONFIRMED' AND PR.event.id = E.id))) " +
            "AND (E.state = 'PUBLISHED') " +
            "AND ((coalesce(:rangeStart, null) is null AND coalesce(:rangeEnd, null ) is null) or (E.eventDate >= now()) ) " +
            "GROUP BY E")
    Page<Event> findAllByParamForPublic(Pageable page,
                                        String text,
                                        List<Integer> categories,
                                        Boolean paid,
                                        LocalDateTime rangeStart,
                                        LocalDateTime rangeEnd,
                                        Boolean onlyAvailable);

    @Query("SELECT E FROM Event AS E " +
            "LEFT JOIN E.requests AS PR " +
            "WHERE (:text is null or lower(E.annotation) like lower(CONCAT('%', :text, '%')) " +
            "or lower(E.description) like lower(CONCAT('%', :text, '%'))) " +
            "AND (:categories is null or E.category.id in (:categories)) " +
            "AND (:paid is null or E.paid in :paid) " +
            "AND (coalesce(:rangeStart, null) is null or E.eventDate >= :rangeStart) " +
            "AND (coalesce(:rangeEnd, null) is null or E.eventDate <= :rangeEnd) " +
            "AND (:onlyAvailable = FALSE or (E.participantLimit = 0 or E.requestModeration = FALSE " +
            "or E.participantLimit > (SELECT count (PR.id) FROM PR WHERE PR.status = 'CONFIRMED' AND PR.event.id = E.id))) " +
            "AND (E.state = 'PUBLISHED') " +
            "AND ((coalesce(:rangeStart, null) is null AND coalesce(:rangeEnd, null) is null) or (E.eventDate >= now()) ) " +
            "GROUP BY E " +
            "ORDER BY E.eventDate DESC ")
    Page<Event> findAllByParamFroPublicWithSort(Pageable page,
                                                String text,
                                                List<Integer> categories,
                                                Boolean paid,
                                                LocalDateTime rangeStart,
                                                LocalDateTime rangeEnd,
                                                Boolean onlyAvailable);

}

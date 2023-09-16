package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.entity.Event;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.model.ConflictException;
import ru.practicum.exception.model.NotFoundException;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public RequestDto createRequest(Long usersId, Long eventId) {
        log.info("Create request. EventId={}, userId={}", eventId, usersId);

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Event with id=" + eventId + " was not found."));

        User user = userRepository.findById(usersId).orElseThrow(
                () -> new NotFoundException("User with id=" + usersId + " was not found."));

        if (event.getInitiator().equals(user)) {
            throw new ConflictException("An event initiator cannot add a request to participate in their event.");
        }

        if (requestRepository.existsByEventIdAndRequesterId(eventId, usersId)) {
            throw new ConflictException("Cannot add a repeat request.");
        }

        int confirmedRequests = requestRepository.countConfirmedRequests(eventId);

        if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == confirmedRequests) {
            throw new ConflictException("The participant limit has been reached.");
        }

        if (!(event.getState() == EventState.PUBLISHED)) {
            throw new ConflictException("You cannot participate in an unpublished event.");
        }

        Request request = new Request(null, event, LocalDateTime.now(), user, null);

        if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
            request.setStatus(RequestStatus.PENDING);
        } else {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        request = requestRepository.save(request);

        return RequestMapper.toDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> findAllByUserId(Long usersId) {
        log.info("Find all request by userId={}", usersId);

        if (!userRepository.existsById(usersId)) {
            throw new NotFoundException("User with id=" + usersId + " was not found.");
        }

        return requestRepository.findByRequesterId(usersId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long usersId, Long requestId) {
        log.info("Cancel request with id={}, userId={}", requestId, usersId);

        if (!userRepository.existsById(usersId)) {
            throw new NotFoundException("User with id=" + usersId + " was not found.");
        }

        Request request = requestRepository.findById(requestId).orElseThrow(
                () -> new NotFoundException("Request with id=" + requestId + " was not found."));

        if (!Objects.equals(request.getRequester().getId(), usersId)) {
            throw new ConflictException("This user did not create this request.");
        }

        request.setStatus(RequestStatus.CANCELED);
        request = requestRepository.save(request);

        return RequestMapper.toDto(request);
    }
}

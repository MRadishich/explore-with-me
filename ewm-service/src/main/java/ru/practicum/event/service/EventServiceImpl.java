package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.entity.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.entity.Comment;
import ru.practicum.event.entity.Event;
import ru.practicum.event.entity.Location;
import ru.practicum.event.enums.EventState;
import ru.practicum.event.enums.SortType;
import ru.practicum.event.enums.StateAction;
import ru.practicum.event.mapper.CommentMapper;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.repository.CommentRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.repository.LocationRepository;
import ru.practicum.exception.model.*;
import ru.practicum.request.dto.RequestDto;
import ru.practicum.request.dto.RequestResultDto;
import ru.practicum.request.dto.RequestUpdateDto;
import ru.practicum.request.enums.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.Request;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.stats.StatisticsService;
import ru.practicum.users.entity.User;
import ru.practicum.users.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final StatisticsService statisticsService;
    private final CommentRepository commentRepository;

    @Value("${app.name}")
    private String app;

    @Override
    @Transactional
    public EventFullDto createEvent(EventCreateDto eventCreateDto, Long userId) {
        log.info("Create an event. EventInputDto = {}, userId = {}", eventCreateDto, userId);

        Event event = EventMapper.toEvent(eventCreateDto);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new BadRequestException("The start of the event should be no earlier than in 2 hours.");
        }

        Category category = categoryRepository.findById(eventCreateDto.getCategory()).orElseThrow(
                () -> new NotFoundException("Category with id=" + eventCreateDto.getCategory() + " not found.")
        );

        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with id=" + userId + " not found.")
        );

        event.setCategory(category);
        event.setInitiator(user);
        event.setState(EventState.PENDING);
        updateLocation(event);

        event = eventRepository.save(event);

        return EventMapper.toFullDto(
                event,
                requestRepository.countConfirmedRequests(event.getId()),
                statisticsService.getViews(event));
    }

    private void updateLocation(Event event) {
        Location location = locationRepository.findByLatAndLon(event.getLocation().getLat(), event.getLocation().getLon());

        event.setLocation(Objects.requireNonNullElseGet(location, () -> locationRepository.save(event.getLocation())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventShortDto> getEventsByUserId(Long userId, int from, int size) {
        log.info("Get events by userId. UserId = {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }

        return eventRepository.findAllByInitiatorId(userId, PageRequest.of(from, size))
                .stream()
                .map(event -> EventMapper.toShortDto(
                        event,
                        requestRepository.countConfirmedRequests(event.getId()),
                        statisticsService.getViews(event)))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, Long userId) {
        log.info("Get events by id and userId. EventId = {},  userId = {}", eventId, userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new ForbiddenException("User with id=" + userId + " did not create event an with id=" + eventId + ".")
        );

        return EventMapper.toFullDto(
                event,
                requestRepository.countConfirmedRequests(event.getId()),
                statisticsService.getViews(event));
    }

    @Override
    @Transactional
    public EventFullDto updateByOwner(EventUpdateUserDto eventUpdateUserDto, Long eventId, Long userId) {
        log.info("Update by owner. EventId={}, userId={}", eventId, userId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new ForbiddenException("User with id=" + userId + " did not create event an with id=" + eventId + ".")
        );

        if (eventUpdateUserDto.getEventDate() != null) {
            if (!eventUpdateUserDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("There must be at least 2 hours before the new event date.");
            }
            event.setEventDate(eventUpdateUserDto.getEventDate());
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ForbiddenException("The start of the event should be no earlier than in 2 hours.");
        }

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        if (eventUpdateUserDto.getStateAction() != null) {
            if (Objects.equals(eventUpdateUserDto.getStateAction(), StateAction.SEND_TO_REVIEW)) {
                event.setState(EventState.PENDING);
            } else if (Objects.equals(eventUpdateUserDto.getStateAction(), StateAction.CANCEL_REVIEW)) {
                event.setState(EventState.CANCELED);
            }
        }

        event = eventRepository.save(event);

        return EventMapper.toFullDto(
                event,
                requestRepository.countConfirmedRequests(eventId),
                statisticsService.getViews(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDto> getUserRequests(Long userId, Long eventId) {
        log.info("Get requests. UserId={}, eventId={}", userId, eventId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with id=" + eventId + " not found.");
        }

        return requestRepository.findByEventId(eventId)
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestResultDto updateRequestStatus(RequestUpdateDto requestUpdateDto, Long userId, Long eventId) {
        log.info("Update request status. UserId={}, eventId={}", userId, eventId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found.");
        }

        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new ForbiddenException("User with id=" + userId + " did not create event an with id=" + eventId + ".")
        );

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new NoChangesRequired("Confirmation of applications is not required.");
        }

        if (requestUpdateDto.getStatus() == RequestStatus.REJECTED) {
            return rejectRequest(requestUpdateDto.getRequestIds());
        }

        if (event.getParticipantLimit() == requestRepository.countConfirmedRequests(event.getId())) {
            throw new ConflictException("The participant limit has been reached.");
        }

        if (requestUpdateDto.getStatus() == RequestStatus.CONFIRMED) {
            return confirmRequest(event, requestUpdateDto.getRequestIds());
        }

        throw new ConflictException("Invalid condition.");
    }

    private RequestResultDto rejectRequest(List<Long> requestIds) {
        RequestResultDto result = new RequestResultDto();
        requestIds.stream()
                .mapToLong(requestId -> requestId)
                .mapToObj(requestId -> requestRepository.findById(requestId)
                        .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found.")))
                .forEach(request -> {
                    checkRequestStatus(request);
                    request.setStatus(RequestStatus.REJECTED);
                    result.getRejectedRequests().add(RequestMapper.toDto(request));
                });
        return result;
    }

    private void checkRequestStatus(Request request) {
        if (request.getStatus().equals(RequestStatus.REJECTED) || request.getStatus().equals(RequestStatus.CONFIRMED)) {
            throw new ConflictException("The status can only be changed for applications that are in a pending state.");
        }
    }

    private RequestResultDto confirmRequest(Event event, List<Long> requestIds) {
        RequestResultDto result = new RequestResultDto();
        long confirmedRequests = requestRepository.countConfirmedRequests(event.getId());

        for (long requestId : requestIds) {
            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found."));

            if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == confirmedRequests) {
                request.setStatus(RequestStatus.REJECTED);
                requestRepository.save(request);
                result.getRejectedRequests().add(RequestMapper.toDto(request));
                continue;
            }

            request.setStatus(RequestStatus.CONFIRMED);
            requestRepository.save(request);
            result.getConfirmedRequests().add(RequestMapper.toDto(request));
            confirmedRequests++;
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByParamForAdmin(
            List<Long> users,
            List<EventState> states,
            List<Integer> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            int from,
            int size) {
        log.info("Get all events by parameters (admin).");

        Pageable page = PageRequest.of(from == 0 ? 0 : from / size, size);

        if (rangeStart != null && rangeEnd != null && rangeStart.isAfter(rangeEnd)) {
            throw new BadRequestException("The start date cannot be later than the end date.");
        }

        return eventRepository.findAllByParamForAdmin(users, states, categories, rangeStart, rangeEnd, page)
                .stream()
                .map(event -> EventMapper.toFullDto(
                        event,
                        requestRepository.countConfirmedRequests(event.getId()),
                        statisticsService.getViews(event)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto adminUpdate(EventUpdateAdminDto eventUpdateAdminDto, Long eventId) {
        log.info("Update event with id = " + eventId + " (admin).");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id =" + eventId + " was not found."));

        if (!event.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
            throw new ConflictException("The start of the event should be no earlier than in 2 hours.");
        }

        if (eventUpdateAdminDto.getAnnotation() != null && !eventUpdateAdminDto.getAnnotation().isBlank()) {
            event.setAnnotation(eventUpdateAdminDto.getAnnotation());
        }

        if (eventUpdateAdminDto.getCategory() != null) {
            event.setCategory(categoryRepository.findById(eventUpdateAdminDto.getCategory())
                    .orElseThrow(() -> new NotFoundException("Category with id=" + eventUpdateAdminDto.getCategory() + " was not found.")));
        }

        if (eventUpdateAdminDto.getDescription() != null && !eventUpdateAdminDto.getDescription().isBlank()) {
            event.setDescription(eventUpdateAdminDto.getDescription());
        }

        if (eventUpdateAdminDto.getEventDate() != null) {
            if (!eventUpdateAdminDto.getEventDate().isAfter(LocalDateTime.now().plusHours(2))) {
                throw new BadRequestException("There must be at least 2 hours before the new event date.");
            }
            event.setEventDate(eventUpdateAdminDto.getEventDate());
        }

        if (eventUpdateAdminDto.getLocation() != null) {
            event.setLocation(eventUpdateAdminDto.getLocation());
        }

        if (eventUpdateAdminDto.getLocation() != null) {
            Location location = locationRepository.findByLatAndLon(eventUpdateAdminDto.getLocation().getLat(), eventUpdateAdminDto.getLocation().getLon());
            if (location == null) {
                location = locationRepository.save(eventUpdateAdminDto.getLocation());
            }
            event.setLocation(location);
        }

        if (eventUpdateAdminDto.getPaid() != null) {
            event.setPaid(eventUpdateAdminDto.getPaid());
        }

        if (eventUpdateAdminDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateAdminDto.getParticipantLimit());
        }

        if (eventUpdateAdminDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateAdminDto.getRequestModeration());
        }

        if (eventUpdateAdminDto.getTitle() != null && !eventUpdateAdminDto.getTitle().isBlank()) {
            event.setTitle(eventUpdateAdminDto.getTitle());
        }

        if (eventUpdateAdminDto.getStateAction() != null) {
            StateAction stateAction = eventUpdateAdminDto.getStateAction();

            if (stateAction.equals(StateAction.PUBLISH_EVENT)) {
                if (!event.getState().equals(EventState.PENDING)) {
                    throw new ConflictException("An event can only be published if it is in a pending publication state.");
                }
                event.setState(EventState.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if (stateAction.equals(StateAction.REJECT_EVENT)) {
                if (event.getState().equals(EventState.PUBLISHED)) {
                    throw new ConflictException("An event can only be rejected if it has not yet been published.");
                }
                event.setState(EventState.CANCELED);
            } else {
                throw new ConflictException("Invalid state.");
            }
        }

        return EventMapper.toFullDto(
                eventRepository.save(event),
                requestRepository.countConfirmedRequests(eventId),
                statisticsService.getViews(event));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventFullDto> getAllByParamForPublic(int from,
                                                     int size,
                                                     String text,
                                                     List<Integer> categories,
                                                     Boolean paid,
                                                     LocalDateTime rangeStart,
                                                     LocalDateTime rangeEnd,
                                                     Boolean onlyAvailable,
                                                     String sort,
                                                     HttpServletRequest httpServletRequest) {
        log.info("Get all events by parameters (public).");
        statisticsService.sendHit(app, httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());

        if (rangeStart != null && rangeEnd != null) {
            if (rangeStart.isAfter(rangeEnd)) {
                throw new BadRequestException("The start date cannot be later than the end date.");
            }
        }

        Pageable page = PageRequest.of(from == 0 ? 0 : from / size, size);

        if (SortType.from(sort).isPresent()) {
            if (sort.equals(SortType.EVENT_DATE.name())) {
                return eventRepository.findAllByParamFroPublicWithSort(page, text, categories, paid, rangeStart, rangeEnd, onlyAvailable)
                        .stream()
                        .map(event -> EventMapper.toFullDto(
                                event,
                                requestRepository.countConfirmedRequests(event.getId()),
                                statisticsService.getViews(event)))
                        .collect(Collectors.toList());
            }
        }

        return eventRepository.findAllByParamForPublic(page, text, categories, paid, rangeStart, rangeEnd, onlyAvailable)
                .stream()
                .map(event -> EventMapper.toFullDto(
                        event,
                        requestRepository.countConfirmedRequests(event.getId()),
                        statisticsService.getViews(event)))
                .sorted(Comparator.comparing(EventFullDto::getViews)
                        .reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EventFullDto getEventById(Long eventId, HttpServletRequest httpServletRequest) {
        log.info("Get all events by id = " + eventId);
        statisticsService.sendHit(app, httpServletRequest.getRequestURI(), httpServletRequest.getRemoteAddr());

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with eventId=" + eventId + " was not found."));

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " not available.");
        }

        return EventMapper.toFullDto(
                event,
                requestRepository.countConfirmedRequests(eventId),
                statisticsService.getViews(event));
    }

    @Override
    @Transactional
    public CommentFullDto createComment(CommentInputDto commentInputDto, Long userId, Long eventId) {
        log.info("Create comment. User id = {}, event id = {}", userId, eventId);

        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found.");
        }

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event with eventId=" + eventId + " was not found.");
        }

        Comment comment = Comment.builder()
                .eventId(eventId)
                .userId(userId)
                .createDate(LocalDateTime.now())
                .text(commentInputDto.getText())
                .build();

        return CommentMapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentFullDto updateComment(CommentInputDto commentInputDto, Long commentId, Long userId) {
        log.info("Update comment. Comment id = {}, user id = {}", commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found."));

        if (!Objects.equals(user.getId(), comment.getUserId())) {
            throw new ConflictException("User with id=" + userId + " did not write a comment with id=" + commentId + ".");
        }

        comment.setText(commentInputDto.getText());
        comment.setUpdateDate(LocalDateTime.now());

        return CommentMapper.toFullDto(commentRepository.save(comment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentShortDto> getCommentsByEventId(Long eventId) {
        log.info("Get all comments. Event id = {}", eventId);

        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Event wih id=" + eventId + " was not found.");
        }

        List<Comment> comments = commentRepository.findAllByEventId(eventId);

        return comments.stream()
                .map(CommentMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteCommentPrivate(Long commentId, Long userId) {
        log.info("Delete comment (private). User id = {}, comment id = {}", userId, commentId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found."));

        if (!Objects.equals(user.getId(), comment.getUserId())) {
            throw new ConflictException("User with id=" + userId + " did not write a comment with id=" + comment + ".");
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public void deleteCommentAdmin(Long commentId) {
        log.info("Delete comment (admin). Comment id = {}", commentId);

        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found.");
        }

        commentRepository.deleteById(commentId);
    }
}

package ru.practicum.users.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.users.entity.User;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByName(String name);

    List<User> findByIdIn(Collection<Long> userIds, Pageable pageable);
}

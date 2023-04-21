package ru.wolves.bookingsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.wolves.bookingsite.models.AdminUser;

import java.util.Optional;

public interface AdminRepo extends JpaRepository<AdminUser,Long> {
    Optional<AdminUser> findByUsername(String username);
}

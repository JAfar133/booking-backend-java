package ru.wolves.bookingsite.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.wolves.bookingsite.models.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepo extends JpaRepository<Token, Long> {

    @Query("""
            select t from Token t inner join Person p
            on t.person.id = p.id where p.id = :personId
            and (t.expired = false or t.revoked = false)
           """)
    List<Token> findAllValidTokensByPerson(Long personId);

    Optional<Token> findByToken(String token);
}

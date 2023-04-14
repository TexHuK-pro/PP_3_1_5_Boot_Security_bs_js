package ru.kata.spring.boot_security.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u FROM User u  join fetch u.roles where u.email =:username")
    User findByUserName(String username);

    @Query("select u FROM User u join fetch u.roles where u.id =:id")
    Optional<User> findById(Long id);

    //поиск последнего добавленного пользователя
    User findTopByOrderByIdDesc();
}

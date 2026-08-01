package com.chat.app.repository;

import com.chat.app.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    List<User> findByStatus(String status);

    @Query("SELECT u FROM User u WHERE u.status = 'ONLINE'")
    List<User> findAllOnlineUsers();

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id IN :userIds")
    List<User> findAllByIds(@Param("userIds") List<String> userIds);

    @Query("SELECT u FROM User u WHERE u.status = 'ONLINE' AND u.id != :userId")
    List<User> findOtherOnlineUsers(@Param("userId") String userId);
}
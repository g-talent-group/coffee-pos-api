package com.coffee.pos.repository;

import com.coffee.pos.model.RefreshToken;
import com.coffee.pos.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteByUser(User user);

    Optional<RefreshToken> findByToken(String token);
}

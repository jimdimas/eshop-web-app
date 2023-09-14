package com.jimdimas.api.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Integer> {

    @Query("SELECT t FROM Token t WHERE t.refreshToken=?1")
    Optional<Token> findByRefreshToken(String refreshToken);
}

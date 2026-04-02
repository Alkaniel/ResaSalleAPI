package fr.enzogiardinelli.rsa.api.security.refreshToken.repository;

import fr.enzogiardinelli.rsa.api.security.refreshToken.model.RefreshToken;
import fr.enzogiardinelli.rsa.api.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByUser(User user);
}
